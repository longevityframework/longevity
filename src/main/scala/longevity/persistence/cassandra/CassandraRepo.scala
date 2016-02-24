package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Session
import emblem.imports._
import emblem.stringUtil._
import java.util.UUID
import longevity.persistence._
import longevity.subdomain._
import longevity.subdomain.root._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.json4s.CustomSerializer
import org.json4s.JNull
import org.json4s.JString
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** a Cassandra repository for aggregate roots of type `R`.
 *
 * @param rootType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 * @param session the connection to the cassandra database
 */
private[longevity] class CassandraRepo[R <: Root : TypeKey] protected[persistence] (
  rootType: RootType[R],
  subdomain: Subdomain,
  session: Session)
extends BaseRepo[R](rootType, subdomain) {
  repo =>

  private val tableName = camelToUnderscore(typeName(rootTypeKey.tpe))
  private val realizedProps = rootType.keySet.flatMap(_.props) ++ rootType.indexSet.flatMap(_.props)

  createSchema()

  override def create(unpersisted: R) = Future {
    val uuid = UUID.randomUUID
    session.execute(bindInsertStatement(uuid, unpersisted))
    new PState[R](CassandraId(uuid, repo.rootTypeKey), unpersisted)
  }

  override def retrieve(keyVal: KeyVal[R]): Future[Option[PState[R]]] = Future {
    val resultSet = session.execute(bindKeyValSelectStatement(keyVal))
    val rowOption = Option(resultSet.one)
    rowOption.map { row =>
      val id = CassandraId(row.getUUID("id"), repo.rootTypeKey)
      implicit val formats = repo.formats
      implicit val manifest = rootTypeKey.manifest
      val root = Serialization.read[R](row.getString("root"))
      new PState[R](id, root)
    }
  }

  override def update(state: PState[R]): Future[PState[R]] = Future {
    session.execute(bindUpdateStatement(state))
    new PState[R](state.passoc, state.get)
  }

  override def delete(state: PState[R]): Future[Deleted[R]] = Future {
    session.execute(bindDeleteStatement(state))
    new Deleted(state.get, state.assoc)
  }

  override protected def retrievePersistedAssoc(assoc: PersistedAssoc[R]): Future[Option[PState[R]]] = {
    ???
  }

  override protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[PState[R]]] = {
    ???
  }

  private def createSchema(): Unit = {
    createTable()
    createIndexes()
  }

  private def createTable(): Unit = {
    val realizedPropColumns = realizedProps.map(prop =>
      s"  ${columnName(prop)} ${CassandraRepo.basicToCassandraType(prop.typeKey)},"
    ).mkString("\n")
    val createTable = s"""|
    |CREATE TABLE IF NOT EXISTS $tableName (
    |  id uuid,
    |  root text,
    |$realizedPropColumns
    |  PRIMARY KEY (id)
    |)
    |WITH COMPRESSION = { 'sstable_compression': 'SnappyCompressor' };
    |""".stripMargin
    session.execute(createTable)
  }

  private def columnName(prop: Prop[R, _]) = "prop_" + scoredPath(prop)

  private def scoredPath(prop: Prop[R, _]) = prop.path.replace('.', '_')

  private def createIndexes(): Unit = {
    rootType.keySet.foreach { key => createIndex(key.props) }
    rootType.indexSet.foreach { index => createIndex(index.props) }
  }

  private def createIndex(props: Seq[Prop[R, _]]): Unit = {
    val name = indexName(props)
    val columnList = props.map(columnName).mkString(", ")
    val createIndex = s"CREATE INDEX IF NOT EXISTS $name ON $tableName ($columnList);"
    session.execute(createIndex)
  }

  private def indexName(props: Seq[Prop[R, _]]): String = {
    val scoredPaths: Seq[String] = props.map(scoredPath)
    s"""${tableName}_${scoredPaths.mkString("_")}"""
  }

  private val insertStatement: PreparedStatement = {
    val cql = if (realizedProps.isEmpty) {
      s"INSERT INTO $tableName (id, root) VALUES (:id, :root)"
    } else {
      val realizedPropColumnNames = realizedProps.map(columnName)
      val realizedPropColumns = realizedPropColumnNames.mkString(",\n  ")
      val realizedSubstitutions = realizedPropColumnNames.map(c => s":$c").mkString(",\n  ")
      s"""|
      |INSERT INTO $tableName (
      |  id,
      |  root,
      |  $realizedPropColumns
      |) VALUES (
      |  :id,
      |  :root,
      |  $realizedSubstitutions
      |)
      |""".stripMargin
    }
    session.prepare(cql)
  }

  private def bindInsertStatement(uuid: UUID, root: R): BoundStatement = {
    val nonPropValues = Array(uuid, jsonStringForRoot(root))
    val realizedPropValues = realizedProps.map(_.propVal(root).asInstanceOf[AnyRef])
    val values = (nonPropValues ++ realizedPropValues)
    insertStatement.bind(values: _*)
  }

  private def jsonStringForRoot(root: R): String = {
    implicit val formats = repo.formats
    Serialization.write(root)
  }

  private val keyValSelectStatement: Map[Key[R], PreparedStatement] = Map().withDefault { key =>
    val relations = key.props.map(columnName).map(name => s"$name = :$name").mkString("\nAND\n  ")
    val cql = s"""|
    |SELECT * FROM $tableName
    |WHERE
    |  $relations
    |""".stripMargin
    session.prepare(cql)
  }

  private def bindKeyValSelectStatement(keyVal: KeyVal[R]): BoundStatement = {
    val preparedStatement = keyValSelectStatement(keyVal.key)
    val boundStatement = preparedStatement.bind(keyVal.propValSeq: _*)
    boundStatement
  }

  private val updateStatement: PreparedStatement = {
    val cql = if (realizedProps.isEmpty) {
      s"UPDATE $tableName SET root = :root WHERE id = :id"
    } else {
      val realizedPropColumnNames = realizedProps.map(columnName)
      val realizedAssignments = realizedPropColumnNames.map(c => s"$c = :$c").mkString(",\n  ")
      s"""|
      |UPDATE $tableName
      |SET
      |  root = :root,
      |  $realizedAssignments
      |WHERE
      |  id = :id
      |""".stripMargin
    }
    session.prepare(cql)
  }

  private def bindUpdateStatement(state: PState[R]): BoundStatement = {
    val root = state.get
    val json = jsonStringForRoot(root)
    val realizedPropVals = realizedProps.view.toArray.map(_.propVal(root).asInstanceOf[AnyRef])
    val uuid = state.assoc.asInstanceOf[CassandraId[R]].uuid
    val values = (json +: realizedPropVals :+ uuid)
    updateStatement.bind(values: _*)
  }

  private val deleteStatement: PreparedStatement = {
    val cql = s"DELETE FROM $tableName WHERE id = :id"
    session.prepare(cql)
  }

  private def bindDeleteStatement(state: PState[R]): BoundStatement = {
    val boundStatement = deleteStatement.bind
    val uuid = state.assoc.asInstanceOf[CassandraId[R]].uuid
    boundStatement.bind(uuid)
  }

  private case object CharSerializer extends CustomSerializer[Char](format => (
    { case JString(s) => s.head
    },
    { case c: Char => JString(s"$c")
    }))

  private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")

  private case object DateTimeSerializer extends CustomSerializer[DateTime](format => (
    { case JString(s) => formatter.parseDateTime(s)
      case JNull => null
    },
    { case d: DateTime => JString(formatter.print(d))
    }
  ))

  private case object ShorthandSerializer extends CustomSerializer[Any](format => (
    { case JString(s) => s.head
    },
    { case c: Char => JString(s"$c")
    })) {
    
  }

  implicit private val formats =
    Serialization.formats(NoTypeHints) + CharSerializer + DateTimeSerializer + ShorthandSerializer

}

private[longevity] object CassandraRepo {

  private val basicToCassandraType = Map[TypeKey[_], String](
    typeKey[Boolean] -> "boolean",
    typeKey[Char] -> "text",
    typeKey[DateTime] -> "timestamp",
    typeKey[Double] -> "double",
    typeKey[Float] -> "float",
    typeKey[Int] -> "int",
    typeKey[Long] -> "bigint",
    typeKey[String] -> "text")

}

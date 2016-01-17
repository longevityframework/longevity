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

  private[persistence] case class CassandraId(uuid: UUID) extends PersistedAssoc[R] {
    val associateeTypeKey = repo.rootTypeKey
    private[longevity] val _lock = 0
  }

  private val tableName = camelToUnderscore(typeName(rootTypeKey.tpe))
  private val realizedProps = rootType.keySet.flatMap(_.props) ++ rootType.indexSet.flatMap(_.props)

  createSchema()

  override def create(unpersisted: R) = Future {
    val uuid = UUID.randomUUID
    session.execute(bindInsertStatement(uuid, unpersisted))
    new PState[R](CassandraId(uuid), unpersisted)
  }

  override def retrieve(keyVal: KeyVal[R]): Future[Option[PState[R]]] = Future {
    val resultSet = session.execute(bindSelectStatement(keyVal))
    val rowOption = Option(resultSet.one)
    val idRootPairOption = rowOption.map { row =>
      val id = CassandraId(row.getUUID("id"))
      implicit val formats = CassandraRepo.formats
      implicit val manifest = rootTypeKey.manifest
      val root = Serialization.read[R](row.getString("root"))
      id -> root
    }
    idRootPairOption.map { case (id, r) => new PState[R](id, r) }
  }

  override def update(state: PState[R]): Future[PState[R]] = ???

  override def delete(state: PState[R]): Future[Deleted[R]] = ???

  override protected def retrievePersistedAssoc(assoc: PersistedAssoc[R]): Future[Option[PState[R]]] = {
    ???
  }

  override protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[PState[R]]] = {
    ???
  }

  // private lazy val typeNameToTypeKeyMap: Map[String, TypeKey[_ <: Root]] =
  //   repoPool.values.map(_.rootType.rootTypeKey).map(key => key.fullname -> key).toMap

  // def update(persisted: Persisted[R]) = patchUnpersistedAssocs(persisted.get) map { patched =>
  //   session.execute(bindDeleteStatement(key)(key.keyVal(persisted.orig)))
  //   session.execute(bindInsertStatement(patched))
  //   new Persisted[R](CassandraId(key.keyVal(patched)), patched)
  // }

  // def delete(persisted: Persisted[R]) = Future {
  //   session.execute(bindDeleteStatement(key)(key.keyVal(persisted.orig)))
  //   new Deleted(persisted)
  // }

  private def createSchema(): Unit = {
    createTable()
    createIndexes()
  }

  private def createTable(): Unit = {
    val realizedPropColumns = realizedProps.map(prop =>
      s"  ${columnName(prop)} ${CassandraRepo.basicToCassandraType(prop.typeKey)},"
    ).mkString("\n")
    val createTable =
      s"""|CREATE TABLE IF NOT EXISTS $tableName (
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
    implicit val formats = CassandraRepo.formats
    Serialization.write(root)
  }

  // TODO memoize this
  private def selectStatement(key: Key[R]): PreparedStatement = {
    val relations = key.props.map(columnName).map(name => s"$name = :$name").mkString("\nAND\n  ")
    val cql = s"""|SELECT * FROM $tableName
                  |WHERE
                  |  $relations
                  |""".stripMargin
    session.prepare(cql)
  }

  private def bindSelectStatement(keyVal: KeyVal[R]): BoundStatement = {
    val preparedStatement = selectStatement(keyVal.key)
    val boundStatement = preparedStatement.bind(keyVal.propValSeq: _*)
    boundStatement
  }

  // private val deleteStatement: PreparedStatement = {
  //   val relations = keyColumns.map(c => s"$c = :$c").mkString("\nAND\n  ")
  //   val cql = s"""|DELETE FROM $tableName
  //                 |WHERE
  //                 |  $relations
  //                 |""".stripMargin
  //   session.prepare(cql)
  // }

  // // TODO please, clean this up
  // private def bindDeleteStatement(key: Key[R])(keyVal: key.Val): BoundStatement = {
  //   val boundStatement = deleteStatement.bind
  //   keyProps.foreach { prop =>
  //     prop.typeKey match {
  //       case b if b == typeKey[Boolean] =>
  //         boundStatement.setBool(columnName(prop), keyVal(prop).asInstanceOf[Boolean])
  //       case b if b == typeKey[Char] =>
  //         boundStatement.setString(columnName(prop), keyVal(prop).toString)
  //       case b if b == typeKey[org.joda.time.DateTime] =>
  //         val javaDate = keyVal(prop).asInstanceOf[DateTime].toDate
  //         boundStatement.setTimestamp(columnName(prop), javaDate)
  //       case b if b == typeKey[Double] =>
  //         boundStatement.setDouble(columnName(prop), keyVal(prop).asInstanceOf[Double])
  //       case b if b == typeKey[Float] =>
  //         boundStatement.setFloat(columnName(prop), keyVal(prop).asInstanceOf[Float])          
  //       case b if b == typeKey[Int] =>
  //         boundStatement.setInt(columnName(prop), keyVal(prop).asInstanceOf[Int])
  //       case b if b == typeKey[Long] =>
  //         boundStatement.setLong(columnName(prop), keyVal(prop).asInstanceOf[Long])
  //       case b if b == typeKey[String] =>
  //         boundStatement.setString(columnName(prop), keyVal(prop).asInstanceOf[String])          
  //     }
  //   }
  //   boundStatement
  // }

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

  private case object CharSerializer extends CustomSerializer[Char](format => (
    {
      case JString(s) => s.head
    },
    {
      case c: Char => JString(s"$c")
    }
  ))

  private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")

  private case object DateTimeSerializer extends CustomSerializer[DateTime](format => (
    {
      case JString(s) => formatter.parseDateTime(s)
      case JNull => null
    },
    {
      case d: DateTime => JString(formatter.print(d))
    }
  ))

  private val formats = Serialization.formats(NoTypeHints) + CharSerializer + DateTimeSerializer

}

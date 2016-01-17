package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Session
import emblem.imports._
import emblem.stringUtil._
//import longevity.exceptions.subdomain.SubdomainException
import longevity.persistence._
import longevity.subdomain._
import longevity.subdomain.root._
import org.joda.time.DateTime
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

  createSchema()

  override def create(unpersisted: R): Future[PState[R]] = ???

  override def retrieve(keyVal: KeyVal[R]): Future[Option[PState[R]]] = ???

  override def update(state: PState[R]): Future[PState[R]] = ???

  override def delete(state: PState[R]): Future[Deleted[R]] = ???

  override protected def retrievePersistedAssoc(assoc: PersistedAssoc[R]): Future[Option[PState[R]]] = {
    ???
  }

  override protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[PState[R]]] = {
    ???
  }

  // private[persistence] val key = rootType.keys.head
  // private val keyProps = key.props.toSeq.sortBy(_.path)
  // private val keyColumns = keyProps.map(columnName(_))

  // private lazy val typeNameToTypeKeyMap: Map[String, TypeKey[_ <: Root]] =
  //   repoPool.values.map(_.rootType.rootTypeKey).map(key => key.fullname -> key).toMap

  // private[persistence] case class CassandraId(keyVal: key.Val) extends PersistedAssoc[R] {
  //   val associateeTypeKey = repo.rootTypeKey
  //   private[longevity] val _lock = 0
  //   def retrieve = repo.retrieve(key)(keyVal).map(_.get)
  // }

  // def create(unpersisted: Unpersisted[R]) = getSessionCreationOrElse(unpersisted, {
  //   patchUnpersistedAssocs(unpersisted.get) map { patched =>
  //     session.execute(bindInsertStatement(patched))
  //     new Persisted[R](CassandraId(key.keyVal(patched)), patched)
  //   }
  // })

  // def retrieve(key: Key[R])(keyVal: key.Val): Future[Option[Persisted[R]]] = Future {
  //   val resultSet = session.execute(bindSelectStatement(key)(keyVal))
  //   val rowOption = Option(resultSet.one)
  //   val eOption = rowOption.map { row =>
  //     // TODO deserialize[R](row.getBytes("instance"))
  //     ???.asInstanceOf[R]
  //   }
  //   eOption.map { e =>
  //     new Persisted[R](CassandraId(repo.key.keyVal(e)), e)
  //   }
  // }

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
    val realizedProps = rootType.keySet.flatMap(_.props) ++ rootType.indexSet.flatMap(_.props)
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
    println(createTable)
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

  // private val insertStatement: PreparedStatement = {
  //   val substitutions = keyColumns.map(c => s":$c").mkString(", ")
  //   val cql = s"""|INSERT INTO $tableName (
  //                 |  ${keyColumns.mkString(", ")},
  //                 |  instance
  //                 |) VALUES (
  //                 |  $substitutions,
  //                 |  :instance
  //                 |)""".stripMargin
  //   session.prepare(cql)
  // }

  // // TODO please, clean this up
  // private def bindInsertStatement(e: R): BoundStatement = {
  //   val boundStatement = insertStatement.bind
  //   keyProps.foreach { prop =>
  //     prop.typeKey match {
  //       case b if b == typeKey[Boolean] =>
  //         boundStatement.setBool(columnName(prop), prop.keyPropVal(e).asInstanceOf[Boolean])
  //       case b if b == typeKey[Char] =>
  //         boundStatement.setString(columnName(prop), prop.keyPropVal(e).toString)
  //       case b if b == typeKey[org.joda.time.DateTime] =>
  //         val javaDate = prop.keyPropVal(e).asInstanceOf[DateTime].toDate
  //         boundStatement.setTimestamp(columnName(prop), javaDate)
  //       case b if b == typeKey[Double] =>
  //         boundStatement.setDouble(columnName(prop), prop.keyPropVal(e).asInstanceOf[Double])
  //       case b if b == typeKey[Float] =>
  //         boundStatement.setFloat(columnName(prop), prop.keyPropVal(e).asInstanceOf[Float])          
  //       case b if b == typeKey[Int] =>
  //         boundStatement.setInt(columnName(prop), prop.keyPropVal(e).asInstanceOf[Int])
  //       case b if b == typeKey[Long] =>
  //         boundStatement.setLong(columnName(prop), prop.keyPropVal(e).asInstanceOf[Long])
  //       case b if b == typeKey[String] =>
  //         boundStatement.setString(columnName(prop), prop.keyPropVal(e).asInstanceOf[String])          
  //     }
  //   }
  //   boundStatement.setBytes("instance", ???) // TODO TODO
  //   boundStatement
  // }

  // private val selectStatement: PreparedStatement = {
  //   val relations = keyColumns.map(c => s"$c = :$c").mkString("\nAND\n  ")
  //   val cql = s"""|SELECT * FROM $tableName
  //                 |WHERE
  //                 |  $relations
  //                 |""".stripMargin
  //   session.prepare(cql)
  // }

  // // TODO please, clean this up
  // private def bindSelectStatement(key: Key[R])(keyVal: key.Val): BoundStatement = {
  //   val boundStatement = selectStatement.bind
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

}

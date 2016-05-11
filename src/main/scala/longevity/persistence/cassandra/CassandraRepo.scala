package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.datastax.driver.core.Session
import com.typesafe.config.Config
import emblem.TypeKey
import emblem.exceptions.CouldNotTraverseException
import emblem.jsonUtil.dateTimeFormatter
import emblem.stringUtil.camelToUnderscore
import emblem.stringUtil.typeName
import emblem.typeKey
import java.util.UUID
import longevity.exceptions.persistence.NotInSubdomainTranslationException
import longevity.persistence.BaseRepo
import longevity.persistence.PState
import longevity.subdomain.DerivedPType
import longevity.subdomain.PolyPType
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.PType
import longevity.subdomain.ptype.Prop
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** a Cassandra repository for persistent entities of type `P`.
 *
 * @param pType the type of the persistent entities this repository handles
 * @param subdomain the subdomain containing the persistent that this repo persists
 * @param session the connection to the cassandra database
 */
private[longevity] class CassandraRepo[P <: Persistent] private (
  pType: PType[P],
  subdomain: Subdomain,
  protected val session: Session)
extends BaseRepo[P](pType, subdomain)
with CassandraSchema[P]
with CassandraCreate[P]
with CassandraRetrieveAssoc[P]
with CassandraRetrieveKeyVal[P]
with CassandraRetrieveQuery[P]
with CassandraUpdate[P]
with CassandraDelete[P] {

  protected[cassandra] val tableName = typeKeyToTableName(pTypeKey)

  protected def typeKeyToTableName(key: TypeKey[_]) = camelToUnderscore(typeName(key.tpe))

  protected[cassandra] def realizedProps: List[Prop[_ >: P <: Persistent, _]] =
    (pType.keySet.flatMap(_.props) ++ pType.indexSet.flatMap(_.props)).toList

  protected val shorthandPool = subdomain.shorthandPool

  protected val persistentToJsonTranslator = new PersistentToJsonTranslator(subdomain.emblematic)
  protected val jsonToPersistentTranslator = new JsonToPersistentTranslator(subdomain.emblematic)

  protected def columnName(prop: Prop[_, _]) = "prop_" + scoredPath(prop)

  protected def scoredPath(prop: Prop[_, _]) = prop.path.replace('.', '_')

  protected def jsonStringForP(p: P): String = {
    try {
      import org.json4s.native.JsonMethods._
      compact(render(persistentToJsonTranslator.traverse(p)(pTypeKey)))
    } catch {
      case e: CouldNotTraverseException =>
        throw new NotInSubdomainTranslationException(e.typeKey, e)
    }
  }

  protected def updateColumnNames(includeId: Boolean = true): Seq[String] = {
    val realizedPropColumnNames = realizedProps.map(columnName).toSeq.sorted
    if (includeId)
      "id" +: "p" +: realizedPropColumnNames
    else
      "p" +: realizedPropColumnNames
  }

  protected def updateColumnValues(uuid: UUID, p: P, includeId: Boolean = true): Seq[AnyRef] = {
    val realizedPropValues = realizedProps.toSeq.sortBy(columnName).map { prop =>
      def bind[PP >: P <: Persistent](prop: Prop[PP, _]) = propValBinding(prop, p)
      bind(prop)
    }
    if (includeId)
      uuid +: jsonStringForP(p) +: realizedPropValues
    else
      jsonStringForP(p) +: realizedPropValues
  }

  protected def propValBinding[PP >: P <: Persistent, A](prop: Prop[PP, A], p: P): AnyRef = {
    def bind[B : TypeKey](prop: Prop[PP, B]) = cassandraValue(prop.propVal(p))
    bind(prop)(prop.propTypeKey)
  }

  protected def cassandraValue[A : TypeKey](value: A): AnyRef = {
    val abbreviated = value match {
      case actual if shorthandPool.contains[A] => shorthandPool[A].abbreviate(actual)
      case a => a
    }
    abbreviated match {
      case id: CassandraId[_] => id.uuid
      case char: Char => char.toString
      case d: DateTime => dateTimeFormatter.print(d)
      case _ => abbreviated.asInstanceOf[AnyRef]
    }
  }

  protected def retrieveFromBoundStatement(
    statement: BoundStatement)(
    implicit context: ExecutionContext)
  : Future[Option[PState[P]]] =
    Future {
      val resultSet = blocking {
        session.execute(statement)
      }
      val rowOption = Option(resultSet.one)
      rowOption.map(retrieveFromRow)
    }

  protected def retrieveFromRow(row: Row): PState[P] = {
    val id = CassandraId[P](row.getUUID("id"))
    import org.json4s.native.JsonMethods._    
    val json = parse(row.getString("p"))
    val p = jsonToPersistentTranslator.traverse[P](json)(pTypeKey)
    new PState[P](id, p)
  }

}

private[persistence] object CassandraRepo {

  def sessionFromConfig(config: Config): Session = {
    val builder = Cluster.builder.addContactPoint(config.getString("cassandra.address"))
    if (config.getBoolean("cassandra.useCredentials")) {
      builder.withCredentials(
        config.getString("cassandra.username"),
        config.getString("cassandra.password"))
    }
    val cluster = builder.build
    val session = cluster.connect()
    val keyspace = config.getString("cassandra.keyspace")
    val replicationFactor = config.getInt("cassandra.replicationFactor")
    session.execute(
      s"""|
      |CREATE KEYSPACE IF NOT EXISTS $keyspace
      |WITH replication = {
      |  'class': 'SimpleStrategy',
      |  'replication_factor': $replicationFactor
      |};
      |""".stripMargin)
    session.execute(s"use $keyspace")
    session
  }

  def apply[P <: Persistent](
    pType: PType[P],
    subdomain: Subdomain,
    session: Session,
    polyRepoOpt: Option[CassandraRepo[_ >: P <: Persistent]])
  : CassandraRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new CassandraRepo(pType, subdomain, session) with PolyCassandraRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P <: Persistent](poly: CassandraRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: CassandraRepo[Poly] = poly
          }
          with CassandraRepo(pType, subdomain, session) with DerivedCassandraRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new CassandraRepo(pType, subdomain, session)
    }
    repo.createSchema()
    repo
  }

  private[cassandra] val basicToCassandraType = Map[TypeKey[_], String](
    typeKey[Boolean] -> "boolean",
    typeKey[Char] -> "text",
    typeKey[DateTime] -> "text",
    typeKey[Double] -> "double",
    typeKey[Float] -> "float",
    typeKey[Int] -> "int",
    typeKey[Long] -> "bigint",
    typeKey[String] -> "text")

}

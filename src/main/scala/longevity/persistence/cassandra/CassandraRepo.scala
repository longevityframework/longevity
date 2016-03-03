package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import emblem.imports._
import emblem.jsonUtil.dateTimeFormatter
import emblem.stringUtil._
import java.util.UUID
import longevity.persistence._
import longevity.subdomain._
import longevity.subdomain.root._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.concurrent.ExecutionContext
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
  protected val session: Session)
extends BaseRepo[R](rootType, subdomain)
with CassandraSchema[R]
with CassandraCreate[R]
with CassandraRetrieveAssoc[R]
with CassandraRetrieveKeyVal[R]
with CassandraRetrieveQuery[R]
with CassandraUpdate[R]
with CassandraDelete[R] {

  protected val tableName = camelToUnderscore(typeName(rootTypeKey.tpe))
  protected val realizedProps = rootType.keySet.flatMap(_.props) ++ rootType.indexSet.flatMap(_.props)
  protected val emblemPool = subdomain.entityEmblemPool
  protected val shorthandPool = subdomain.shorthandPool

  private val extractorPool = shorthandPoolToExtractorPool(shorthandPool)
  private val rootToJsonTranslator = new RootToJsonTranslator(emblemPool, extractorPool)
  private val jsonToRootTranslator = new JsonToRootTranslator(emblemPool, extractorPool)

  protected def columnName(prop: Prop[R, _]) = "prop_" + scoredPath(prop)

  protected def scoredPath(prop: Prop[R, _]) = prop.path.replace('.', '_')

  protected def jsonStringForRoot(root: R): String = {
    import org.json4s.native.JsonMethods._    
    compact(render(rootToJsonTranslator.traverse(root)))
  }

  protected def propValBinding[A](prop: Prop[R, A], root: R): AnyRef = {
    def bind[B : TypeKey](prop: Prop[R, B]) = cassandraValue(prop.propVal(root))
    bind(prop)(prop.typeKey)
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
  : Future[Option[PState[R]]] =
    Future {
      val resultSet = session.execute(statement)
      val rowOption = Option(resultSet.one)
      rowOption.map(retrieveFromRow)
    }

  protected def retrieveFromRow(row: Row): PState[R] = {
    val id = CassandraId[R](row.getUUID("id"))
    import org.json4s.native.JsonMethods._    
    val json = parse(row.getString("root"))
    val root = jsonToRootTranslator.traverse[R](json)
    new PState[R](id, root)
  }

  createSchema()

}

private[cassandra] object CassandraRepo {

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

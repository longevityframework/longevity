package longevity.persistence.cassandra

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import emblem.imports._
import emblem.stringUtil._
import java.util.UUID
import longevity.exceptions.persistence.cassandra.NeqInQueryException
import longevity.exceptions.persistence.cassandra.OrInQueryException
import longevity.persistence._
import longevity.subdomain._
import longevity.subdomain.root._
import longevity.subdomain.root.Query._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.collection.JavaConversions._
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
  protected val session: Session)
extends BaseRepo[R](rootType, subdomain) with CassandraSchema[R] {
  repo =>

  // TODO DRY this is in EmblemToJsonTranslator and JsonToEmblemTranslator too
  private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")

  protected val tableName = camelToUnderscore(typeName(rootTypeKey.tpe))
  protected val realizedProps = rootType.keySet.flatMap(_.props) ++ rootType.indexSet.flatMap(_.props)
  protected val emblemPool = subdomain.entityEmblemPool
  protected val shorthandPool = subdomain.shorthandPool
  private val extractorPool = shorthandPoolToExtractorPool(shorthandPool)
  private val rootToJsonTranslator = new RootToJsonTranslator(emblemPool, extractorPool)
  private val jsonToRootTranslator = new JsonToRootTranslator(emblemPool, extractorPool)

  protected def columnName(prop: Prop[R, _]) = "prop_" + scoredPath(prop)
  protected def scoredPath(prop: Prop[R, _]) = prop.path.replace('.', '_')

  createSchema()

  override def create(unpersisted: R) = Future {
    val uuid = UUID.randomUUID
    session.execute(bindInsertStatement(uuid, unpersisted))
    new PState[R](CassandraId(uuid, repo.rootTypeKey), unpersisted)
  }

  override def retrieve(keyVal: KeyVal[R]): Future[Option[PState[R]]] =
    retrieveFromBoundStatement(bindKeyValSelectStatement(keyVal))

  override def update(state: PState[R]): Future[PState[R]] = Future {
    session.execute(bindUpdateStatement(state))
    new PState[R](state.passoc, state.get)
  }

  override def delete(state: PState[R]): Future[Deleted[R]] = Future {
    session.execute(bindDeleteStatement(state))
    new Deleted(state.get, state.assoc)
  }

  override protected def retrievePersistedAssoc(assoc: PersistedAssoc[R]): Future[Option[PState[R]]] =
    retrieveFromBoundStatement(bindIdSelectStatement(assoc.asInstanceOf[CassandraId[R]]))

  private case class QueryInfo(whereClause: String, bindValues: Seq[AnyRef])

  override protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[PState[R]]] =
    Future {
      val info = queryInfo(query)
      val cql = s"SELECT * FROM $tableName WHERE ${info.whereClause} ALLOW FILTERING"
      val preparedStatement = session.prepare(cql)
      val boundStatement = preparedStatement.bind(info.bindValues: _*)
      val resultSet = session.execute(boundStatement)
      resultSet.all.toList.map(retrieveFromRow)
    }

  private def queryInfo(query: ValidatedQuery[R]): QueryInfo = {
    query match {
      case VEqualityQuery(prop, op, value) => op match {
        case EqOp => QueryInfo(s"${columnName(prop)} = :${columnName(prop)}",
                               Seq(cassandraValue(value)(prop.typeKey)))
        case NeqOp => throw new NeqInQueryException
      }
      case VOrderingQuery(prop, op, value) => op match {
        case LtOp => QueryInfo(s"${columnName(prop)} < :${columnName(prop)}",
                               Seq(cassandraValue(value)(prop.typeKey)))
        case LteOp => QueryInfo(s"${columnName(prop)} <= :${columnName(prop)}",
                                Seq(cassandraValue(value)(prop.typeKey)))
        case GtOp => QueryInfo(s"${columnName(prop)} > :${columnName(prop)}",
                               Seq(cassandraValue(value)(prop.typeKey)))
        case GteOp => QueryInfo(s"${columnName(prop)} >= :${columnName(prop)}",
                                Seq(cassandraValue(value)(prop.typeKey)))
      }
      case VConditionalQuery(lhs, op, rhs) => op match {
        case AndOp =>
          val lhsQueryInfo = queryInfo(lhs)
          val rhsQueryInfo = queryInfo(rhs)
          QueryInfo(s"${lhsQueryInfo.whereClause} AND ${rhsQueryInfo.whereClause}",
                    lhsQueryInfo.bindValues ++ rhsQueryInfo.bindValues)
        case OrOp => throw new OrInQueryException
      }
    }
  }

  private def cassandraValue[A : TypeKey](value: A): AnyRef = {
    val abbreviated = value match {
      case actual if shorthandPool.contains[A] => shorthandPool[A].abbreviate(actual)
      case a => a
    }
    abbreviated match {
      case id: CassandraId[_] => id.uuid
      case char: Char => char.toString
      case d: DateTime => formatter.print(d)
      case _ => value.asInstanceOf[AnyRef]
    }
  }

  private val insertStatement: PreparedStatement = {
    val cql = if (realizedProps.isEmpty) {
      s"INSERT INTO $tableName (id, root) VALUES (:id, :root)"
    } else {
      val realizedPropColumnNames = realizedProps.map(columnName).toSeq.sorted
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
    val realizedPropValues = realizedProps.toSeq.sortBy(columnName).map(propValBinding(_, root))
    val values = (nonPropValues ++ realizedPropValues)
    insertStatement.bind(values: _*)
  }

  private def propValBinding(prop: Prop[R, _], root: R): AnyRef = {
    def rawValue[A](prop: Prop[R, A]) = prop.propVal(root)
    val abbreviated = if (shorthandPool.contains(prop.typeKey)) {
      def abbrevValue[A : TypeKey](value: A) = shorthandPool(typeKey[A]).abbreviate(value)
      def abbrevProp[A : TypeKey](prop: Prop[R, A]) = abbrevValue(rawValue(prop))(prop.typeKey)
      abbrevProp(prop)
    } else {
      rawValue(prop)
    }
    // TODO use cassandraValue here
    abbreviated match {
      case CassandraId(uuid, _) => uuid
      case c: Char => c.toString
      case d: DateTime => formatter.print(d)
      case x => x.asInstanceOf[AnyRef]
    }
  }

  private def jsonStringForRoot(root: R): String = {
    import org.json4s.native.JsonMethods._    
    compact(render(rootToJsonTranslator.traverse(root)))
  }

  private val keyValSelectStatement: Map[Key[R], PreparedStatement] = Map().withDefault { key =>
    val relations = key.props.map(columnName).map(name => s"$name = :$name").mkString("\nAND\n  ")
    val cql = s"""|
    |SELECT * FROM $tableName
    |WHERE
    |  $relations
    |ALLOW FILTERING
    |""".stripMargin
    session.prepare(cql)
  }

  private def bindKeyValSelectStatement(keyVal: KeyVal[R]): BoundStatement = {
    val preparedStatement = keyValSelectStatement(keyVal.key)
    val assocKey = typeKey[Assoc[_ <: Root]]
    // TODO: we have this weird kind of switch on Prop all over the place. encapsulate in prop.
    // the prop can have the shorthand pool
    val propVals = keyVal.key.props.collect {
      case p if p.typeKey <:< assocKey =>
        keyVal(p).asInstanceOf[CassandraId[R]].uuid
      case p if shorthandPool.contains(p.typeKey) =>
        def abbrev[A : TypeKey](prop: Prop[R, A]) =
          shorthandPool(typeKey[A]).abbreviate(keyVal[A](prop))
        abbrev(p)(p.typeKey).asInstanceOf[AnyRef]
      case p =>
        keyVal(p).asInstanceOf[AnyRef]
    }
    val boundStatement = preparedStatement.bind(propVals: _*)
    boundStatement
  }

  private def retrieveFromBoundStatement(statement: BoundStatement): Future[Option[PState[R]]] =
    Future {
      val resultSet = session.execute(statement)
      val rowOption = Option(resultSet.one)
      rowOption.map(retrieveFromRow)
    }

  private def retrieveFromRow(row: Row): PState[R] = {
    val id = CassandraId(row.getUUID("id"), repo.rootTypeKey)
    import org.json4s.native.JsonMethods._    
    val json = parse(row.getString("root"))
    val root = jsonToRootTranslator.traverse[R](json)
    new PState[R](id, root)
  }

  private val updateStatement: PreparedStatement = {
    val cql = if (realizedProps.isEmpty) {
      s"UPDATE $tableName SET root = :root WHERE id = :id"
    } else {
      val realizedPropColumnNames = realizedProps.toSeq.map(columnName).sorted
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
    val realizedPropVals = realizedProps.toArray.sortBy(columnName).map(propValBinding(_, root))
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

  private val idSelectStatement = {
    val cql = s"SELECT * FROM $tableName WHERE id = :id"
    session.prepare(cql)
  }

  private def bindIdSelectStatement(assoc: CassandraId[R]): BoundStatement = {
    idSelectStatement.bind(assoc.uuid)
  }

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

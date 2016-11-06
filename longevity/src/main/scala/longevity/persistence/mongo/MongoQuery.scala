package longevity.persistence.mongo

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.mongodb.client.MongoCursor
import com.mongodb.client.model.Filters
import longevity.persistence.PState
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.query.AndOp
import longevity.subdomain.query.Ascending
import longevity.subdomain.query.ConditionalFilter
import longevity.subdomain.query.Descending
import longevity.subdomain.query.EqOp
import longevity.subdomain.query.FilterAll
import longevity.subdomain.query.GtOp
import longevity.subdomain.query.GteOp
import longevity.subdomain.query.LtOp
import longevity.subdomain.query.LteOp
import longevity.subdomain.query.NeqOp
import longevity.subdomain.query.OrOp
import longevity.subdomain.query.Query
import longevity.subdomain.query.QueryFilter
import longevity.subdomain.query.QueryOrderBy
import longevity.subdomain.query.RelationalFilter
import longevity.subdomain.query.RelationalOp
import longevity.subdomain.ptype.Prop
import longevity.subdomain.realized.RealizedPartitionKey
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.BsonString
import org.bson.BsonValue
import org.bson.conversions.Bson
import scala.collection.JavaConversions.asScalaIterator
import scala.collection.immutable.VectorBuilder
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking

/** implementation of MongoRepo.retrieveByQuery and MongoRepo.streamByQuery */
private[mongo] trait MongoQuery[P <: Persistent] {
  repo: MongoRepo[P] =>

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext)
  : Future[Seq[PState[P]]] = Future {
    logger.debug(s"calling MongoRepo.retrieveByQuery: $query")
    val states = blocking {
      val cursor = queryCursor(query)
      val builder = new VectorBuilder[PState[P]]()
      while (cursor.hasNext) {
        builder += bsonToState(cursor.next)
      }
      builder.result()
    }
    logger.debug(s"done calling MongoRepo.retrieveByQuery: $states")
    states
  }

  def streamByQueryImpl(query: Query[P]): Source[PState[P], NotUsed] = {
    logger.debug(s"calling MongoRepo.streamByQuery: $query")
    val source = Source.fromIterator { () => queryCursor(query).map(bsonToState) }
    logger.debug(s"done calling MongoRepo.streamByQuery: $source")
    source
  }

  private def queryCursor(query: Query[P]): MongoCursor[BsonDocument] = {
    val filter         = mongoFilter(query.filter)
    val filteredCursor = mongoCollection.find(filter)
    val orderBy        = mongoOrderBy(query.orderBy)
    val orderByCursor  = orderBy.map(o => filteredCursor.sort(o)).getOrElse(filteredCursor)
    val offsetCursor   = query.offset.map(orderByCursor.skip).getOrElse(orderByCursor)
    val limitCursor    = query.limit.map(offsetCursor.limit).getOrElse(offsetCursor)
    logger.debug(
      s"calling MongoCollection.find: filter = $filter orderBy = $orderBy " +
      s"offset = ${query.offset} limit = ${query.limit}")
    limitCursor.iterator
  }

  protected def mongoFilter(filter: QueryFilter[P]): Bson = {
    filter match {
      case FilterAll() => doc("$comment", new BsonString("matching FilterAll"))
      case RelationalFilter(prop, op, value) => mongoRelationalFilter(prop, op, value)
      case ConditionalFilter(lhs, op, rhs) => op match {
        case AndOp => Filters.and(mongoFilter(lhs), mongoFilter(rhs))
        case OrOp  => Filters.or(mongoFilter(lhs), mongoFilter(rhs))
      }
    }
  }

  // TODO also used by MongoRetrieve
  protected def mongoRelationalFilter[A](prop: Prop[_ >: P <: Persistent, A], op: RelationalOp, value: A) = {
    realizedPType.partitionKey match {
      case Some(k) if !k.fullyPartitioned && k.key.keyValProp == prop =>
        def f[V <: KeyVal[P, V]](k: RealizedPartitionKey[P, V]) =
          mongoRelationalFilterForPartitionKey[V](k, op, value.asInstanceOf[V])
        f(k)
      case _ =>
        op match {
          case EqOp  => Filters.eq (prop.path, propValToMongo(value, prop))
          case NeqOp => Filters.ne (prop.path, propValToMongo(value, prop))
          case LtOp  => Filters.lt (prop.path, propValToMongo(value, prop))
          case LteOp => Filters.lte(prop.path, propValToMongo(value, prop))
          case GtOp  => Filters.gt (prop.path, propValToMongo(value, prop))
          case GteOp => Filters.gte(prop.path, propValToMongo(value, prop))
        }
    }
  }

  private def mongoRelationalFilterForPartitionKey[V <: KeyVal[P, V]](
    key: RealizedPartitionKey[P, V],
    op: RelationalOp,
    value: V) = {
    val propPaths = key.queryInfos

    def translate[B](pp: key.QueryInfo[B], value: V) =
      subdomainToBsonTranslator.translate(pp.get(value), false)(pp.typeKey)

    def eqPP[B](pp: key.QueryInfo[B]) = Filters.eq(pp.inlinedPath, translate(pp, value))
    def nePP[B](pp: key.QueryInfo[B]) = Filters.ne(pp.inlinedPath, translate(pp, value))
    def ltPP[B](pp: key.QueryInfo[B]) = Filters.lt(pp.inlinedPath, translate(pp, value))
    def gtPP[B](pp: key.QueryInfo[B]) = Filters.gt(pp.inlinedPath, translate(pp, value))

    def and(fs: Seq[Bson]) = Filters.and(fs: _*)
    def or (fs: Seq[Bson]) = Filters.or (fs: _*)

    def eq = and { propPaths.map { pp => eqPP(pp) } }
    def ne = or  { propPaths.map { pp => nePP(pp) } }

    def diff(f: (key.QueryInfo[_]) => Bson) = or {      
      for { i <- 0 until propPaths.length } yield {
        and {
          propPaths.take(i).map(f) :+ f(propPaths(i))
        }
      }
    }
 
    def lt = diff { pp => ltPP(pp) }
    def gt = diff { pp => gtPP(pp) }

    op match {
      case EqOp  => eq
      case NeqOp => ne
      case LtOp  => lt
      case LteOp => Filters.or(lt, eq)
      case GtOp  => gt
      case GteOp => Filters.or(gt, eq)
    }

  }

  private def doc(key: String, value: BsonValue) = new BsonDocument(key, value)

  private def mongoOrderBy(orderBy: QueryOrderBy[P]): Option[BsonDocument] = {
    if (orderBy == QueryOrderBy.empty) None else {
      val document = new BsonDocument()
      orderBy.sortExprs.foreach { sortExpr =>
        val propPath = sortExpr.prop.path
        val direction = sortExpr.direction match {
          case Ascending => 1
          case Descending => -1
        }
        document.append(propPath, new BsonInt32(direction))
      }
      Some(document)
    }
  }

}

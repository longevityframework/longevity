package longevity.persistence.mongo

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.mongodb.casbah.MongoCursor
import com.mongodb.casbah.commons.Implicits.unwrapDBObj
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import longevity.persistence.PState
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
      queryCursor(query).toSeq.map(dbObjectToPState)
    }
    logger.debug(s"done calling MongoRepo.retrieveByQuery: $states")
    states
  }

  def streamByQueryImpl(query: Query[P]): Source[PState[P], NotUsed] = {
    logger.debug(s"calling MongoRepo.streamByQuery: $query")
    val source = Source.fromIterator { () => queryCursor(query).map(dbObjectToPState) }
    logger.debug(s"done calling MongoRepo.streamByQuery: $source")
    source
  }

  private def queryCursor(query: Query[P]): MongoCursor = {
    val filter         = mongoFilter(query.filter)
    val filteredCursor = mongoCollection.find(filter)
    val orderBy        = mongoOrderBy(query.orderBy)
    val orderByCursor  = orderBy.map(o => filteredCursor.sort(o)).getOrElse(filteredCursor)
    val offsetCursor   = query.offset.map(orderByCursor.skip).getOrElse(orderByCursor)
    val limitCursor    = query.limit.map(offsetCursor.limit).getOrElse(offsetCursor)
    logger.debug(
      s"calling MongoCollection.find: filter = $filter orderBy = $orderBy " +
      s"offset = ${query.offset} limit = ${query.limit}")
    limitCursor
  }

  protected def mongoFilter(filter: QueryFilter[P]): MongoDBObject = {
    filter match {
      case FilterAll() => MongoDBObject("$comment" -> "matching FilterAll")
      case RelationalFilter(prop, op, value) => op match {
        case EqOp  => MongoDBObject(prop.path -> propValToMongo(value, prop))
        case NeqOp => MongoDBObject(prop.path -> MongoDBObject("$ne" -> propValToMongo(value, prop)))
        case LtOp  => MongoDBObject(prop.path -> MongoDBObject("$lt" -> propValToMongo(value, prop)))
        case LteOp => MongoDBObject(prop.path -> MongoDBObject("$lte" -> propValToMongo(value, prop)))
        case GtOp  => MongoDBObject(prop.path -> MongoDBObject("$gt" -> propValToMongo(value, prop)))
        case GteOp => MongoDBObject(prop.path -> MongoDBObject("$gte" -> propValToMongo(value, prop)))
      }
      case ConditionalFilter(lhs, op, rhs) => op match {
        case AndOp => MongoDBObject("$and" -> Seq(mongoFilter(lhs), mongoFilter(rhs)))
        case OrOp  => MongoDBObject("$or" -> Seq(mongoFilter(lhs), mongoFilter(rhs)))
      }
    }
  }

  private def mongoOrderBy(orderBy: QueryOrderBy[P]): Option[MongoDBObject] = {
    if (orderBy == QueryOrderBy.empty) None else {
      val builder = new MongoDBObjectBuilder()
      orderBy.sortExprs.foreach { sortExpr =>
        val propPath = sortExpr.prop.path
        val direction = sortExpr.direction match {
          case Ascending => 1
          case Descending => -1
        }
        builder += propPath -> direction
      }
      Some(builder.result())
    }
  }

}

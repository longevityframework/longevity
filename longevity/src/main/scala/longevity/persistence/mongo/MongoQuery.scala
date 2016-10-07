package longevity.persistence.mongo

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.mongodb.casbah.MongoCursor
import com.mongodb.casbah.commons.Implicits.unwrapDBObj
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
import longevity.persistence.PState
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.ConditionalFilter
import longevity.subdomain.ptype.RelationalFilter
import longevity.subdomain.ptype.Query
import longevity.subdomain.ptype.QueryFilter
import longevity.subdomain.ptype.QueryFilter.All
import longevity.subdomain.ptype.QueryFilter.AndOp
import longevity.subdomain.ptype.QueryFilter.EqOp
import longevity.subdomain.ptype.QueryFilter.GtOp
import longevity.subdomain.ptype.QueryFilter.GteOp
import longevity.subdomain.ptype.QueryFilter.LtOp
import longevity.subdomain.ptype.QueryFilter.LteOp
import longevity.subdomain.ptype.QueryFilter.NeqOp
import longevity.subdomain.ptype.QueryFilter.OrOp
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
    val casbah = mongoQuery(query.filter)
    logger.debug(s"calling MongoCollection.find: $casbah")
    mongoCollection.find(casbah)
  }

  protected def mongoQuery(filter: QueryFilter[P]): MongoDBObject = {
    filter match {
      case All() => MongoDBObject("$comment" -> "matching QueryFilter.All")
      case RelationalFilter(prop, op, value) => op match {
        case EqOp  => MongoDBObject(prop.path -> propValToMongo(value, prop))
        case NeqOp => MongoDBObject(prop.path -> MongoDBObject("$ne" -> propValToMongo(value, prop)))
        case LtOp  => MongoDBObject(prop.path -> MongoDBObject("$lt" -> propValToMongo(value, prop)))
        case LteOp => MongoDBObject(prop.path -> MongoDBObject("$lte" -> propValToMongo(value, prop)))
        case GtOp  => MongoDBObject(prop.path -> MongoDBObject("$gt" -> propValToMongo(value, prop)))
        case GteOp => MongoDBObject(prop.path -> MongoDBObject("$gte" -> propValToMongo(value, prop)))
      }
      case ConditionalFilter(lhs, op, rhs) => op match {
        case AndOp => MongoDBObject("$and" -> Seq(mongoQuery(lhs), mongoQuery(rhs)))
        case OrOp  => MongoDBObject("$or" -> Seq(mongoQuery(lhs), mongoQuery(rhs)))
      }
    }
  }

}

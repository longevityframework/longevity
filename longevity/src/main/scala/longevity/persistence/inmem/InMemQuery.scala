package longevity.persistence.inmem

import akka.NotUsed
import akka.stream.scaladsl.Source
import longevity.persistence.PState
import longevity.subdomain.Persistent
import longevity.subdomain.query.Query
import longevity.subdomain.query.QueryFilter
import longevity.subdomain.query.QueryOrderBy
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of InMemRepo.retrieveByQuery and streamByQuery */
private[inmem] trait InMemQuery[P <: Persistent] {
  repo: InMemRepo[P] =>

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext)
  : Future[Seq[PState[P]]] =
    Future.successful {
      logger.debug(s"calling InMemRepo.retrieveByQuery: $query")
      val states = queryResults(query)
      logger.debug(s"done calling InMemRepo.retrieveByQuery: $states")
      states
    }

  def streamByQueryImpl(query: Query[P]): Source[PState[P], NotUsed] = {
    logger.debug(s"calling InMemRepo.streamByQuery: $query")
    val source = Source.fromIterator { () => queryResults(query).iterator }
    logger.debug(s"done calling InMemRepo.streamByQuery: $source")
    source
  }

  private def queryResults(query: Query[P]): Seq[PState[P]] = {
    val matches = allPStates.filter { s =>
      QueryFilter.matches(query.filter, s.get, realizedPType)
    }
    implicit val pOrdering = QueryOrderBy.ordering(query.orderBy, realizedPType)
    implicit val pStateOrdering = scala.math.Ordering.by { pstate: PState[P] => pstate.get }
    val orderedMatches = matches.sorted
    val offsetMatches = query.offset match {
      case Some(o) => orderedMatches.drop(o.toInt)
      case None => orderedMatches
    }
    val limitMatches = query.limit match {
      case Some(l) => offsetMatches.take(l.toInt)
      case None => offsetMatches
    }
    limitMatches
  }

  protected[inmem] def allPStates: Seq[PState[P]] = idToPStateMap.values.view.toSeq

}

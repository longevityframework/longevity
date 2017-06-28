package longevity.persistence.inmem

import longevity.persistence.PState
import longevity.model.query.Query
import longevity.model.query.QueryFilter
import longevity.model.query.QueryOrderBy
import streamadapter.Chunkerator

/** implementation of InMemPRepo.retrieveByQuery and streamByQuery */
private[inmem] trait InMemQuery[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  protected def queryToChunkerator(query: Query[P]): Chunkerator[PState[P]] = {
    logger.debug(s"calling InMemPRepo.queryToChunkerator: $query")
    val states = queryResults(query)
    val chunkerator = Chunkerator.grouped(10, states)
    logger.debug(s"done calling InMemPRepo.queryToChunkerator: $states")
    chunkerator
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

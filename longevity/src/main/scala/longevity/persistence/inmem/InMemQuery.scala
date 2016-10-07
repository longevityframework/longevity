package longevity.persistence.inmem

import akka.NotUsed
import akka.stream.scaladsl.Source
import longevity.persistence.PState
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.Query
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

  private def queryResults(query: Query[P]): Seq[PState[P]] =
    allPStates.filter { s => InMemRepo.queryFilterMatches(query.filter, s.get, realizedPType) }

  protected[inmem] def allPStates: Seq[PState[P]] = idToPStateMap.values.view.toSeq

}

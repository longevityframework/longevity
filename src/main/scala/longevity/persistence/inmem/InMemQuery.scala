package longevity.persistence.inmem

import akka.NotUsed
import akka.stream.scaladsl.Source
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Query
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of InMemRepo.retrieveByQuery and streamByQuery */
private[inmem] trait InMemQuery[P <: Persistent] {
  repo: InMemRepo[P] =>

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext)
  : Future[Seq[PState[P]]] =
    Future.successful(queryResults(query))

  def streamByQueryImpl(query: Query[P]): Source[PState[P], NotUsed] =
    Source.fromIterator { () => queryResults(query).iterator }

  private def queryResults(query: Query[P]): Seq[PState[P]] =
    allPStates.filter { s => InMemRepo.queryMatches(query, s.get, realizedPType) }

  protected[inmem] def allPStates: Seq[PState[P]] = idToPStateMap.values.view.toSeq

}

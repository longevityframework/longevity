package longevity.persistence.inmem

import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of InMemRepo.create */
private[inmem] trait InMemCreate[P <: Persistent] {
  repo: InMemRepo[P] =>

  def create(unpersisted: P)(implicit context: ExecutionContext) = Future {
    persist(IntId[P](nextId), unpersisted)
  }

}

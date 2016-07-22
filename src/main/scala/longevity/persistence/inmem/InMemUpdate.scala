package longevity.persistence.inmem

import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of InMemRepo.update */
private[inmem] trait InMemUpdate[P <: Persistent] {
  repo: InMemRepo[P] =>

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    repo.synchronized {
      dumpKeys(state.orig)
    }
    try {
      persist(state.id, state.get)
    } catch {
      case e: DuplicateKeyValException[_] =>
        repo.synchronized {
          keys.foreach { key =>
            registerPStateByKeyVal(key.keyValForP(state.orig), state)
          }
          throw e
        }
    }
  }

}

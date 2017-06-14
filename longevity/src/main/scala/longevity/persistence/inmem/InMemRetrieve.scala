package longevity.persistence.inmem

import longevity.model.ptype.Key
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of InMemPRepo.retrieve */
private[inmem] trait InMemRetrieve[M, P] {
  repo: InMemPRepo[M, P] =>

  override def retrieve[V : Key[M, P, ?]](keyVal: V)(implicit context: ExecutionContext) =
    Future.successful {
      logger.debug(s"calling InMemPRepo.retrieve: $keyVal")
      val stateOption = lookupPStateByKeyVal(keyVal)
      logger.debug(s"done calling InMemPRepo.retrieve: $stateOption")
      stateOption
    }

}

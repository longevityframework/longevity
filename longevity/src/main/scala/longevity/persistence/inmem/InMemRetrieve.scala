package longevity.persistence.inmem

import longevity.model.ptype.Key
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of InMemRepo.retrieve */
private[inmem] trait InMemRetrieve[M, P] {
  repo: InMemRepo[M, P] =>

  override def retrieve[V : Key[M, P, ?]](keyVal: V)(implicit context: ExecutionContext) =
    Future.successful {
      logger.debug(s"calling InMemRepo.retrieve: $keyVal")
      val stateOption = lookupPStateByKeyVal(keyVal)
      logger.debug(s"done calling InMemRepo.retrieve: $stateOption")
      stateOption
    }

}

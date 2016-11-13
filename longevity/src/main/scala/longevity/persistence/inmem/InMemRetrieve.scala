package longevity.persistence.inmem

import emblem.TypeKey
import longevity.subdomain.KeyVal
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** implementation of InMemRepo.retrieve */
private[inmem] trait InMemRetrieve[P] {
  repo: InMemRepo[P] =>

  override def retrieve[V <: KeyVal[P] : TypeKey](keyVal: V)(implicit context: ExecutionContext) =
    Future.successful {
      logger.debug(s"calling InMemRepo.retrieve: $keyVal")
      val stateOption = lookupPStateByKeyVal(keyVal)
      logger.debug(s"done calling InMemRepo.retrieve: $stateOption")
      stateOption
    }

}

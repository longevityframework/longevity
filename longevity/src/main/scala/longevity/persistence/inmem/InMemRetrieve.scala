package longevity.persistence.inmem

import longevity.model.ptype.Key

/** implementation of InMemPRepo.retrieve */
private[inmem] trait InMemRetrieve[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  def retrieve[V : Key[M, P, ?]](v: V) = effect.map(effect.pure(v)) { v =>
    logger.debug(s"calling InMemPRepo.retrieve: $v")
    val stateOption = lookupPStateByKeyVal(v)
    logger.debug(s"done calling InMemPRepo.retrieve: $stateOption")
    stateOption
  }

}

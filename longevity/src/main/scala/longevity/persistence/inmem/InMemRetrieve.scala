package longevity.persistence.inmem

import longevity.model.ptype.Key

/** implementation of InMemPRepo.retrieve */
private[inmem] trait InMemRetrieve[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  def retrieve[V : Key[M, P, ?]](v: V) = effect.map(effect.pure(v)) { v =>
    logger.debug(s"executing InMemPRepo.retrieve: $v")
    val stateOption = lookupPStateByKeyVal(v)
    logger.debug(s"done executing InMemPRepo.retrieve: $stateOption")
    stateOption
  }

}

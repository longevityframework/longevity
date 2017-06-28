package longevity.persistence.inmem

import longevity.persistence.PState

/** support for InMemPRepo methods that read from persistent collection. used by
 * [[InMemRetrieve]] and [[InMemQuery]].
 */
private[inmem] trait InMemRead[F[_], M, P] {
  repo: InMemPRepo[F, M, P] =>

  protected[inmem] def lookupPStateByKeyVal(keyVal: Any): Option[PState[P]] =
    keyValToPStateMap.get(keyVal)

}

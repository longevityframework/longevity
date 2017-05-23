package longevity.persistence.inmem

import longevity.persistence.PState

/** support for InMemRepo methods that read from persistent collection. used by
 * [[InMemRetrieve]] and [[InMemQuery]].
 */
private[inmem] trait InMemRead[M, P] {
  repo: InMemRepo[M, P] =>

  protected[inmem] def lookupPStateByKeyVal(keyVal: AnyKeyValAtAll): Option[PState[P]] =
    keyValToPStateMap.get(keyVal)

}

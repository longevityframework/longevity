package longevity.persistence.inmem

import longevity.persistence.PState

/** support for InMemRepo methods that read from persistent collection. used by
 * [[InMemRetrieve]] and [[InMemQuery]].
 */
private[inmem] trait InMemRead[P] {
  repo: InMemRepo[P] =>

  protected[inmem] def lookupPStateByKeyVal(keyVal: AnyKeyValAtAll): Option[PState[P]] =
    keyValToPStateMap.get(keyVal)

}

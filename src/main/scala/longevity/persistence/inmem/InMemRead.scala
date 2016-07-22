package longevity.persistence.inmem

import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent

/** support for InMemRepo methods that read from persistent collection. used by
 * [[InMemRetrieve]] and [[InMemQuery]].
 */
private[inmem] trait InMemRead[P <: Persistent] {
  repo: InMemRepo[P] =>

  protected[inmem] def lookupPStateByKeyVal(keyVal: AnyKeyValAtAll): Option[PState[P]] =
    keyValToPStateMap.get(keyVal)

}

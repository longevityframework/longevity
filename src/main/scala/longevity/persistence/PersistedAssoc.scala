package longevity.persistence

import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Persistent

/** an [[longevity.subdomain.Assoc Assoc]] to a persistent entity that has been
 * persisted
 */
private[longevity] trait PersistedAssoc[P <: Persistent] extends Assoc[P] {
  def isPersisted = true

  /** returns a copy of this persisted assoc with a wider type bound */
  def widen[Q >: P <: Persistent]: PersistedAssoc[Q]

}

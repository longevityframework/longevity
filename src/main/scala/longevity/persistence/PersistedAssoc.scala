package longevity.persistence

import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Persistent

/** an [[longevity.subdomain.Assoc Assoc]] to a persistent entity that has been
 * persisted
 */
private[longevity] trait PersistedAssoc[P <: Persistent] extends Assoc[P] {
  def isPersisted = true
}

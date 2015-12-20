package longevity.persistence

import longevity.subdomain._
import longevity.exceptions.subdomain.AssocIsPersistedException

/** an [[longevity.subdomain.Assoc Assoc]] to a root that has been persisted */
private[longevity] trait PersistedAssoc[R <: Root] extends Assoc[R] {
  def isPersisted = true
  def unpersisted = throw new AssocIsPersistedException(this)
}

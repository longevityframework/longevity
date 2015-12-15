package longevity.persistence

import longevity.subdomain._
import longevity.exceptions.subdomain.AssocIsPersistedException

/** an [[longevity.subdomain.Assoc Assoc]] to a root that has been persisted.
 *
 * right now, this is exposed in [[Persisted]] and [[Repo]] APIs. this will be fixed with pt-87652430,
 * at which point, we should be able to make this trait `private[persistence]`.
 */
trait PersistedAssoc[R <: Root] extends Assoc[R] {
  def isPersisted = true
  def unpersisted = throw new AssocIsPersistedException(this)
}

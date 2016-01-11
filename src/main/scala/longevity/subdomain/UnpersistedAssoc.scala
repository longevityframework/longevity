package longevity.subdomain

import emblem.TypeKey
import emblem.typeKey

/** an [[Assoc]] to a root that has not been persisted. for use with
 * [[longevity.persistence.RepoPool.createMany]]
 */
private[longevity] case class UnpersistedAssoc[R <: Root : TypeKey](unpersisted: R) extends Assoc[R] {
  val associateeTypeKey = typeKey[R]
  private[longevity] val _lock = 0
  def isPersisted = false
}

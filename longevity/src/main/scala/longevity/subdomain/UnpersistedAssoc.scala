package longevity.subdomain

import emblem.TypeKey
import emblem.typeKey
import longevity.exceptions.subdomain.AssocIsUnpersistedException

/** an [[Assoc]] to a root entity that has not been persisted */
case class UnpersistedAssoc[R <: RootEntity : TypeKey](unpersisted: R) extends Assoc[R] {
  val associateeTypeKey = typeKey[R]
  private[longevity] val _lock = 0
  def isPersisted = false
  def retrieve = throw new AssocIsUnpersistedException(this)
}

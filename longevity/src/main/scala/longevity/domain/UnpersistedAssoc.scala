package longevity.domain

import emblem.TypeKey
import emblem.typeKey
import longevity.exceptions.AssocIsUnpersistedException

// TODO: scaladoc
case class UnpersistedAssoc[E <: RootEntity : TypeKey](unpersisted: E) extends Assoc[E] {
  val associateeTypeKey = typeKey[E]
  private[longevity] val _lock = 0
  def isPersisted = false
  def retrieve = throw new AssocIsUnpersistedException(this)
}

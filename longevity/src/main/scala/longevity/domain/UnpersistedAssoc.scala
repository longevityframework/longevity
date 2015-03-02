package longevity.domain

import emblem.TypeKey
import emblem.typeKey

// TODO: scaladoc
case class UnpersistedAssoc[E <: Entity : TypeKey](unpersisted: E) extends Assoc[E] {
  val associateeTypeKey = typeKey[E]
  private[longevity] val _lock = 0
  def isPersisted = false
  def retrieve = throw new Assoc.AssocIsUnpersistedException(this)
}

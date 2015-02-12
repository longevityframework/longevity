package longevity.domain

import emblem.TypeKey
import emblem.typeKey

// TODO: scaladoc
case class UnpersistedAssoc[E <: Entity : TypeKey](unpersisted: E) extends Assoc[E] {
  val associateeTypeTag = typeKey[E].tag
  private[longevity] val _lock = 0
  def isPersisted = false
  def retrieve = throw new Assoc.AssocIsUnpersistedException(this)
}

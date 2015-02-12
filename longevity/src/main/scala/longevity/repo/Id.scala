package longevity.repo

import longevity.domain._

object Id {
  implicit def assocToId[E <: Entity](assoc: Assoc[E]): Id[E] = assoc.asInstanceOf[Id[E]]
}

// TODO consider rename to PersistedAssoc
trait Id[E <: Entity] extends Assoc[E] {
  def isPersisted = true
  def unpersisted = throw new Assoc.AssocIsPersistedException(this)
}

package longevity.repo

import longevity.domain._

// TODO scaladoc
// TODO shouldnt this be private[longevity.repo]?
object Id {

  implicit def assocToId[E <: RootEntity](assoc: Assoc[E]): Id[E] = assoc.asInstanceOf[Id[E]]

}

// TODO rename to PersistedAssoc
// TODO scaladoc
// TODO shouldnt this be private[longevity.repo]?
trait Id[E <: RootEntity] extends Assoc[E] {
  def isPersisted = true
  def unpersisted = throw new Assoc.AssocIsPersistedException(this)
}

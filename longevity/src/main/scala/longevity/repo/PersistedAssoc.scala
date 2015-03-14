package longevity.repo

import longevity.domain._
import longevity.exceptions.AssocIsPersistedException

// TODO scaladoc
// TODO shouldnt this be private[longevity.repo]?
object PersistedAssoc {

  implicit def assocToPersistedAssoc[E <: RootEntity](assoc: Assoc[E]): PersistedAssoc[E] =
    assoc.asInstanceOf[PersistedAssoc[E]]

}

// TODO scaladoc
// TODO shouldnt this be private[longevity.repo]?
trait PersistedAssoc[E <: RootEntity] extends Assoc[E] {
  def isPersisted = true
  def unpersisted = throw new AssocIsPersistedException(this)
}

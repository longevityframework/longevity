package longevity.repo

import longevity.domain._
import longevity.exceptions.AssocIsPersistedException

// TODO scaladoc
// TODO shouldnt this be private[longevity.repo]?
//private[repo]
 trait PersistedAssoc[E <: RootEntity] extends Assoc[E] {
  def isPersisted = true
  def unpersisted = throw new AssocIsPersistedException(this)
}

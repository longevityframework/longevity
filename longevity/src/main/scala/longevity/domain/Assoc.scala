package longevity.domain

import scala.language.implicitConversions

object Assoc {

  /** wraps an entity in a AssocWithUnpersisted when needed */
  implicit def apply[E <: Entity](e: E): Assoc[E] = AssocWithUnpersisted(e)

  class AssocIsUnpersistedException[E <: Entity](val assoc: Assoc[E])
  extends Exception("cannot retrieve from an unpersisted assoc")

  class AssocIsPersistedException[E <: Entity](val assoc: Assoc[E])
  extends Exception("cannot get an unpersisted entity from a persisted assoc")

}

/** an association between two domain entities */
trait Assoc[E <: Entity] {

  /** prevent subtyping outside of longevity library */
  private[longevity] val _lock: Int

  /** TODO scaladoc */
  def isPersisted: Boolean

  /** TODO scaladoc */
  def retrieve: E

  /** TODO scaladoc */
  def unpersisted: E

  /** TODO scaladoc */
  def get: E
}

case class AssocWithUnpersisted[E <: Entity](unpersisted: E) extends Assoc[E] {
  private[longevity] val _lock = 0
  def isPersisted = false
  def retrieve = throw new Assoc.AssocIsUnpersistedException(this)
  def get = unpersisted
}


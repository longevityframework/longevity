package longevity.domain

import scala.reflect.runtime.universe._
import scala.language.implicitConversions
import longevity.repo.RetrieveResult

object Assoc {

  /** wraps an entity in an unpersisted assoc. this is useful for building out domain data
   * that has not been persisted. it is made implicit so your code isn't littered with `Assoc(_)` calls
   * everywhere. this ought not to be confusing, as there is no other sensible way to embed one entity into
   * another. */
  implicit def apply[E <: Entity : TypeTag](e: E): Assoc[E] = UnpersistedAssoc(e)

  class AssocIsUnpersistedException[E <: Entity](val assoc: Assoc[E])
  extends Exception("cannot retrieve from an unpersisted assoc")

  class AssocIsPersistedException[E <: Entity](val assoc: Assoc[E])
  extends Exception("cannot get an unpersisted entity from a persisted assoc")

}

/** a unidirectional association, or "assoc", between two domain entities. the left side of the association -
 * that is, the holder of the `Assoc` instance - is known as the associator. the right side of the association
 * is the associatee.
 *
 * there are basically two kinds of associations that you have to understand. an unpersisted assoc is one in
 * which the associatee has not been persisted. in this case, an attempt to persist the associator will cascade
 * persist the associatee.
 *
 * a persisted assoc is one in which the associatee has already been persisted. it may or may not have already
 * been loaded in to program memory, and calling `retrieve` or `persisted` or `get` may well trigger a database
 * lookup. */
trait Assoc[E <: Entity] {

  val associateeTypeTag: TypeTag[E]

  /** prevent subtyping outside of longevity library */
  private[longevity] val _lock: Int

  /** true whenever the assoc is with a persisted entity */
  def isPersisted: Boolean

  @throws[Assoc.AssocIsUnpersistedException[E]]("whenever the assoc is not persisted")
  def retrieve: RetrieveResult[E]

  /** retrieves the persisted associatee from the assoc */
  //@inline
  @throws[Assoc.AssocIsUnpersistedException[E]]("whenever the assoc is not persisted")
  final def persisted: E = retrieve.get

  /** retrieves an unpersisted associatee from the assoc */
  @throws[Assoc.AssocIsPersistedException[E]]("whenever the assoc is persisted")
  def unpersisted: E

  /** gets the underlying assoc, whether persisted or not */
  @inline
  final def get: E = if (isPersisted) persisted else unpersisted
}

case class UnpersistedAssoc[E <: Entity : TypeTag](unpersisted: E) extends Assoc[E] {
  val associateeTypeTag = typeTag[E]
  private[longevity] val _lock = 0
  def isPersisted = false
  def retrieve = throw new Assoc.AssocIsUnpersistedException(this)
}


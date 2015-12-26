package longevity.subdomain

import emblem.imports._
import longevity.exceptions.subdomain.AssocIsPersistedException
import longevity.exceptions.subdomain.AssocIsUnpersistedException
import longevity.persistence.PState
import scala.concurrent.Future

object Assoc {

  /** wraps a root in an unpersisted assoc. this is useful for building out domain data
   * that has not been persisted. it is made implicit so your code isn't littered with `Assoc(_)` calls
   * everywhere. this ought not to be confusing, as there is no other sensible way to embed a root into
   * another entity.
   */
  implicit def apply[R <: Root : TypeKey](root: R): Assoc[R] = UnpersistedAssoc(root)

}

/** a unidirectional association, or "assoc", between an entity and an aggregate root. the left side of the
 * association - that is, the holder of the `Assoc` instance - is known as the associator. the right side of
 * the association is the associatee.
 *
 * there are basically two kinds of associations that you have to understand. an unpersisted assoc is one in
 * which the associatee has not been persisted. in this case, an attempt to persist the associator will cascade
 * persist the associatee.
 *
 * a persisted assoc is one in which the associatee has already been persisted. it may or may not have already
 * been loaded in to program memory, and calling `retrieve` or `persisted` or `get` may well trigger a database
 * lookup.
 */
trait Assoc[R <: Root] {

  /** a type key for the associatee */
  val associateeTypeKey: TypeKey[R]

  /** prevent subtyping outside of longevity library */
  private[longevity] val _lock: Int

  /** true whenever the assoc is with a persisted entity */
  def isPersisted: Boolean

  /** retrieves a persisted assoc. if the associatee has not been loaded into memory, calling this method
   * will result in a database lookup
   * 
   * @throws AssocIsUnpersistedException whenever the assoc is not persisted
   */
  def retrieve: Future[PState[R]]

  /** retrieves an unpersisted associatee from the assoc
   * @throws AssocIsPersistedException whenever the assoc is persisted
   */
  def unpersisted: R

}


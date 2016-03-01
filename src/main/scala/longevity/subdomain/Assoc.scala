package longevity.subdomain

import emblem.imports._
import longevity.persistence.PState
import scala.concurrent.Future

object Assoc {

  /** wraps a root in an unpersisted assoc. this is useful for building out
   * domain data that has not been persisted. it is made implicit so your code
   * isn't littered with `Assoc(_)` calls everywhere. this ought not to be
   * confusing, as there is no other sensible way to embed a root into
   * another entity.
   */
  implicit def apply[R <: Root : TypeKey](root: R): Assoc[R] = UnpersistedAssoc(root)

}

/** a unidirectional association, or "assoc", between an entity and an
 * aggregate root. the left side of the association - that is, the holder of the
 * `Assoc` instance - is known as the associator. the right side of
 * the association is the associatee.
 *
 * there are basically two kinds of associations that you have to understand.
 * the main kind is a "persisted assoc", which means an association to an
 * aggregate that has already been persisted. the associated aggregate can be
 * retrieved using [[longevity.persistence.Repo repository method]]
 * `retrieve(Assoc[R])`.
 *
 * the second kind of association is an "unpersisted assoc", which you can use
 * when creating multiple aggregates at once with
 * [[longevity.persistence.RepoPool.createMany]]. unpersisted assocs are
 * intended to support this single use-case. in general we work with persisted
 * assocs.
 */
trait Assoc[R <: Root] {

  /** prevent subtyping outside of longevity library */
  private[longevity] val _lock: Int

  /** true whenever the assoc is with a persisted entity */
  def isPersisted: Boolean

  /** retrieves an unpersisted associatee from the assoc
   * @throws longevity.exceptions.subdomain.AssocIsPersistedException whenever the assoc is persisted
   */
  def unpersisted: R

}


package longevity.subdomain

import emblem.TypeKey
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.Future

object Assoc {

  /** wraps a persistent object in an unpersisted assoc. this is useful for
   * building out domain data that has not been persisted. it is made implicit
   * so your code isn't littered with `Assoc(_)` calls everywhere. this ought
   * not to be confusing, as it normally does not make sense to embed one
   * persistent object within another persistent object.
   */
  implicit def apply[P <: Persistent : TypeKey](p: P): Assoc[P] = UnpersistedAssoc(p)

}

/** a unidirectional association, or "assoc", to a persistent object. the left
 * side of the association - that is, the holder of the `Assoc` instance - is
 * known as the associator. the right side of the association is the associatee.
 *
 * there are basically two kinds of associations that you have to understand.
 * the main kind is a "persisted assoc", which means an association to a
 * persistent object that has already been persisted. the associated object can
 * be retrieved using [[longevity.persistence.Repo repository method]]
 * `retrieve(Assoc[R])`.
 *
 * the second kind of association is an "unpersisted assoc", which you can use
 * when creating multiple persistent objects at once with
 * [[longevity.persistence.RepoPool.createMany]]. unpersisted assocs are
 * intended to support this single use-case. in general we work with persisted
 * assocs.
 */
trait Assoc[P <: Persistent] extends PRef[P] {

  /** true whenever the assoc is with a persisted object */
  def isPersisted: Boolean

}

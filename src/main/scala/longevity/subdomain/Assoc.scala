package longevity.subdomain

import emblem.imports._
import longevity.persistence.PState
import scala.concurrent.Future

object Assoc {

  /** wraps a persistent entity in an unpersisted assoc. this is useful for
   * building out domain data that has not been persisted. it is made implicit
   * so your code isn't littered with `Assoc(_)` calls everywhere. this ought
   * not to be confusing, as it normally does not make sense to embed one
   * persistent entity within another persistent entity.
   */
  implicit def apply[P <: Persistent : TypeKey](p: P): Assoc[P] = UnpersistedAssoc(p)

}

/** a unidirectional association, or "assoc", between an entity and a
 * persistent entity. the left side of the association - that is, the holder of the
 * `Assoc` instance - is known as the associator. the right side of
 * the association is the associatee.
 *
 * there are basically two kinds of associations that you have to understand.
 * the main kind is a "persisted assoc", which means an association to an
 * entity that has already been persisted. the associated entity can be
 * retrieved using [[longevity.persistence.Repo repository method]]
 * `retrieve(Assoc[R])`.
 *
 * the second kind of association is an "unpersisted assoc", which you can use
 * when creating multiple persistent entities at once with
 * [[longevity.persistence.RepoPool.createMany]]. unpersisted assocs are
 * intended to support this single use-case. in general we work with persisted
 * assocs.
 */
trait Assoc[P <: Persistent] extends PRef[P] {

  /** true whenever the assoc is with a persisted entity */
  def isPersisted: Boolean

}

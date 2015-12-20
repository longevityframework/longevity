package longevity.persistence

import longevity.subdomain._

/** the persistent state of an aggregate
 *
 * @param assoc an association to the aggregate
 */
case class PState[R <: Root] private[persistence] (
  val assoc: PersistedAssoc[R],
  private[persistence] val orig: R,
  private val root: R) {

  private[persistence] def this(assoc: PersistedAssoc[R], root: R) = this(assoc, root, root)

  /** returns the aggregate */
  def get: R = root

  /** returns the persistent state of an updated entity */
  def set(root: R): PState[R] = map(_ => root)

  /** returns the persistent state of an entity modified according to function `f` */
  def map(f: R => R): PState[R] = new PState(assoc, orig, f(root))

  // we may want to consider === here if we allow for non-case class entities
  protected def dirty = orig == root

}

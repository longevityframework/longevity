package longevity.persistence

import longevity.subdomain.persistent.Persistent

/** the persistent state of a persistent object of type `P` */
case class PState[P <: Persistent] private[persistence] (
  private[persistence] val id: DatabaseId[P],
  private[persistence] val orig: P,
  private val p: P) {

  private[persistence] def this(id: DatabaseId[P], p: P) = this(id, p, p)

  /** returns the persistent object */
  def get: P = p

  /** returns the persistent state of an updated persistent object */
  def set(p: P): PState[P] = map(_ => p)

  /** returns the persistent state of the persistent object modified according
   * to function `f`
   */
  def map(f: P => P): PState[P] = new PState(id, orig, f(p))

  /** returns true iff there are unpersisted changes to the persistent object */
  // we may want to consider === here if we allow for non-case class entities
  def dirty = orig == p

  /** returns a copy of this persistent state with a wider type bound */
  def widen[Q >: P <: Persistent]: PState[Q] = new PState[Q](id.widen[Q], orig, get)

}

package longevity.persistence

import longevity.subdomain.persistent.Persistent
import org.joda.time.DateTime

/** the persistent state of a persistent object of type `P` */
case class PState[P <: Persistent] private[persistence] (
  private[persistence] val id: DatabaseId[P],
  private[persistence] val modifiedDate: Option[DateTime],
  private[persistence] val orig: P,
  private val p: P) {

  // TODO this one should probably go
  private[persistence] def this(id: DatabaseId[P], p: P) = this(id, None, p, p)

  /** returns the persistent object */
  def get: P = p

  /** returns the persistent state of an updated persistent object */
  def set(p: P): PState[P] = map(_ => p)

  /** returns the persistent state of the persistent object modified according
   * to function `f`
   */
  def map(f: P => P): PState[P] = PState(id, modifiedDate, orig, f(p))

  /** returns true iff there are unpersisted changes to the persistent object */
  // we may want to consider === here if we allow for non-case class entities
  def dirty = orig == p

  /** returns a copy of this persistent state with a wider type bound */
  def widen[Q >: P <: Persistent]: PState[Q] = new PState[Q](id.widen[Q], modifiedDate, orig, get)

}

object PState {

  private[persistence]
  def apply[P <: Persistent](id: DatabaseId[P], modifiedDate: Option[DateTime], p: P): PState[P] =
    PState(id, modifiedDate, p, p)

}

package longevity.persistence

import longevity.subdomain.Persistent

/** the persistent state of a persistent object of type `P` */
case class PState[P <: Persistent] private (
  private[persistence] val id: DatabaseId[P],
  private[persistence] val rowVersion: Option[Long],
  private[persistence] val orig: P,
  private val p: P) {

  /** returns the persistent object */
  def get: P = p

  /** returns the persistent state of an updated persistent object */
  def set(p: P): PState[P] = map(_ => p)

  /** returns the persistent state of the persistent object modified according
   * to function `f`
   */
  def map(f: P => P): PState[P] = PState(id, rowVersion, orig, f(p))

  /** returns a copy of this persistent state with a wider type bound */
  def widen[Q >: P <: Persistent]: PState[Q] = new PState[Q](id.widen[Q], rowVersion, orig, p)

  override def toString = s"PState($p)"

  /** produces a new PState that represents the changes in the current PState
   * having been committed to the database
   */
  private[persistence] def update(optimisticLocking: Boolean): PState[P] = {
    val newRowVersion = if (optimisticLocking) rowVersion.map(_ + 1).orElse(Some(0L)) else None
    copy(orig = p, rowVersion = newRowVersion)
  }

}

object PState {

  private[persistence]
  def apply[P <: Persistent](id: DatabaseId[P], rowVersion: Option[Long], p: P): PState[P] =
    PState(id, rowVersion, p, p)

}

package longevity.persistence


/** the persistent state of a persistent object of type `P` */
case class PState[P] private (
  private[persistence] val id: Option[DatabaseId[P]],
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
  def widen[Q >: P]: PState[Q] = new PState[Q](id.map(_.widen[Q]), rowVersion, orig, p)

  override def toString = s"PState($p)"

  /** produces a new PState that represents the changes in the current PState
   * having been committed to the database
   */
  private[persistence] def update(optimisticLocking: Boolean): PState[P] = {
    val newRowVersion = if (optimisticLocking) rowVersion.map(_ + 1).orElse(Some(0L)) else None
    copy(orig = p, rowVersion = newRowVersion)
  }

  private[persistence] def rowVersionOrNull = rowVersion.asInstanceOf[Option[AnyRef]].orNull

}

object PState {

  private[persistence]
  def apply[P](id: DatabaseId[P], rowVersion: Option[Long], p: P): PState[P] =
    PState(Some(id), rowVersion, p, p)

  private[persistence]
  def apply[P](id: Option[DatabaseId[P]], rowVersion: Option[Long], p: P): PState[P] =
    PState(id, rowVersion, p, p)

}

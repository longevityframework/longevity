package longevity.persistence

import longevity.effect.Effect
import org.joda.time.DateTime

/** the persistent state of a persistent object of type `P` */
case class PState[P] private(
  private[persistence] val id: Option[DatabaseId],
  private[persistence] val rowVersion: Option[Long],
  private[persistence] val createdTimestamp: Option[DateTime],
  private[persistence] val updatedTimestamp: Option[DateTime],
  private[longevity]   val migrationStarted: Boolean = false,
  private[persistence] val migrationComplete: Boolean = false,
  private[persistence] val orig: P,
  private val p: P) {

  /** returns the persistent object */
  def get: P = p

  /** returns the persistent state of an updated persistent object */
  def set(p: P): PState[P] = modify(_ => p)

  /** returns the persistent state of the persistent object modified according to function `f`
   */
  def modify(f: P => P): PState[P] =
    PState(id, rowVersion, createdTimestamp, updatedTimestamp, migrationStarted, migrationComplete, orig, f(p))

  private[longevity] def unmigrate = copy(migrationStarted = false, migrationComplete = false)


  /** returns the persistent state of the persistent object modified according to function `f`.
   *
   * this method is used by longevity migrations. in general, we cannot map a PState, because it
   * doesn't make sense to apply existing persistence information into an object of another type.
   * migrations is a special case where it does make sense.
   */
  private[longevity] def map[P2](f: P => P2): PState[P2] =
    PState(id, rowVersion, createdTimestamp, updatedTimestamp, f(p))

  /** returns the persistent state of the persistent object modified according to an effectful
   * function `f`
   */
  def modifyF[F[_] : Effect](f: P => F[P]): F[PState[P]] = {
    val effect = implicitly[Effect[F]]
    val fp = effect.flatMap(effect.pure(p))(f)
    effect.map(fp) {
      PState(id, rowVersion, createdTimestamp, updatedTimestamp, migrationStarted, migrationComplete, orig, _)
    }
  }

  /** returns a copy of this persistent state with a wider type bound */
  def widen[Q >: P]: PState[Q] = PState[Q](
    id,
    rowVersion,
    createdTimestamp,
    updatedTimestamp,
    migrationStarted,
    migrationComplete,
    orig,
    p)

  override def toString = s"PState($p)"

  /** produces a new PState that represents the changes in the current PState
   * having been committed to the database
   */
  private[persistence] def update(optimisticLocking: Boolean, writeTimestamps: Boolean): PState[P] = {
    val newRowVersion = if (optimisticLocking) rowVersion.map(_ + 1).orElse(Some(0L)) else None
    val newUpdatedDateTime = if (writeTimestamps) Some(DateTime.now) else None
    copy(orig = p, rowVersion = newRowVersion, updatedTimestamp = newUpdatedDateTime)
  }

  private[persistence] def rowVersionOrNull = rowVersion.asInstanceOf[Option[AnyRef]].orNull

  private[persistence] def createdTimestampOrNull = createdTimestamp.asInstanceOf[Option[AnyRef]].orNull

  private[persistence] def updatedTimestampOrNull = updatedTimestamp.asInstanceOf[Option[AnyRef]].orNull

}

private[persistence] object PState {

  private[persistence] def apply[P](
    id: DatabaseId,
    rowVersion: Option[Long],
    createdTimestamp: Option[DateTime],
    updatedTimestamp: Option[DateTime],
    p: P): PState[P] =
    PState(Some(id), rowVersion, createdTimestamp, updatedTimestamp, false, false, p, p)

  private[persistence] def apply[P](
    id: Option[DatabaseId],
    rowVersion: Option[Long],
    createdTimestamp: Option[DateTime],
    updatedTimestamp: Option[DateTime],
    p: P): PState[P] =
    PState(id, rowVersion, createdTimestamp, updatedTimestamp, false, false, p, p)

}

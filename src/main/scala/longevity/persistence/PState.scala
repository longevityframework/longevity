package longevity.persistence

import emblem.TypeKey
import longevity.exceptions.persistence.InvalidPStateException
import longevity.subdomain._

/** the persistent state of an aggregate */
sealed trait PState[R <: Root] {

  protected val root: R

  /** returns the aggregate */
  def get: R = root

  /** returns the persistent state of an updated entity */
  def set(root: R): PState[R] = map(_ => root)

  /** returns the persistent state of an entity modified according to function `f` */
  def map(f: R => R): PState[R]

  /** returns an association to the aggregate */
  def assoc: Assoc[R]

  /** the state name. one of "Unpersisted", "Persisted", and "Deleted" */
  protected def stateName: String

  /** returns `true` whenever this is unpersisted */
  def isUnpersisted: Boolean = false

  /** returns `true` whenever this is persisted */
  def isPersisted: Boolean = false

  /** returns `true` whenever this is deleted */
  def isDeleted: Boolean = false

  /** casts this persistent state to an [[Unpersisted]]
   *
   * @throws longevity.exceptions.persistence.InvalidPStateException
   * if the persistent state is not unpersisted
   */
  def asUnpersisted: Unpersisted[R] = throw new InvalidPStateException("Unpersisted", stateName)

  /** casts this persistent state to an [[Persisted]]
   *
   * @throws longevity.exceptions.persistence.InvalidPStateException
   * if the persistent state is not persisted
   */
  def asPersisted: Persisted[R] = throw new InvalidPStateException("Persisted", stateName)

  /** casts this persistent state to an [[Deleted]]
   *
   * @throws longevity.exceptions.persistence.InvalidPStateException
   * if the persistent state is not deleted
   */
  def asDeleted: Deleted[R] = throw new InvalidPStateException("Deleted", stateName)

}

/** the persistent state of an entity that hasn't been persisted yet. */
case class Unpersisted[R <: Root : TypeKey] private[persistence] (override protected val root: R)
extends PState[R] {

  def map(f: R => R) = new Unpersisted(f(root))

  def assoc = Assoc(root)

  override def stateName = "Unpersisted"
  override def isUnpersisted = true
  override def asUnpersisted = this

}

/** the persistent state of a persisted entity */
case class Persisted[R <: Root] private[persistence] (
  val assoc: PersistedAssoc[R],
  private[persistence] val orig: R,
  protected val root: R)
extends PState[R] {

  private[persistence] def this(assoc: PersistedAssoc[R], root: R) = this(assoc, root, root)

  def map(f: R => R) = new Persisted(assoc, orig, f(root))

  // we may want to consider === here if we allow for non-case class entities
  def dirty = orig == root

  override def stateName = "Persisted"
  override def isPersisted = false
  override def asPersisted = this

}

/** the persistent state of a deleted entity */
case class Deleted[R <: Root] private[persistence] (
  override val assoc: PersistedAssoc[R],
  override protected val root: R)
extends PState[R] {

  def this(p: Persisted[R]) = this(p.assoc, p.orig)

  def map(f: R => R) = new Deleted(assoc, root)

  override def stateName = "Deleted"
  override def isDeleted = false
  override def asDeleted = this

}

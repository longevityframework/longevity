package longevity.persistence

import emblem.TypeKey
import longevity.subdomain._

/** the persistent state of an aggregate */
sealed trait PersistentState[R <: RootEntity] {

  protected val root: R

  /** returns the aggregate */
  def get: R = root

  /** returns the persistent state of an updated entity */
  def set(root: R): PersistentState[R] = map(_ => root)

  /** returns the persistent state of an entity modified according to function `f` */
  def map(f: R => R): PersistentState[R]

  /** returns an association to the aggregate */
  def assoc: Assoc[R]

}

/** the persistent state of an entity that hasn't been persisted yet. */
class Unpersisted[R <: RootEntity : TypeKey] private[persistence] (override protected val root: R)
extends PersistentState[R] {

  def map(f: R => R) = new Unpersisted(f(root))

  def assoc = Assoc(root)

}

/** the persistent state of a persisted entity */
class Persisted[R <: RootEntity] private[persistence] (
  val assoc: PersistedAssoc[R],
  private[persistence] val orig: R,
  protected val root: R
)
extends PersistentState[R] {

  private[persistence] def this(assoc: PersistedAssoc[R], root: R) = this(assoc, root, root)

  def map(f: R => R) = new Persisted(assoc, orig, f(root))

  // we may want to consider === here if we allow for non-case class entities
  def dirty = orig == root

}

/** the persistent state of a deleted entity */
class Deleted[R <: RootEntity] private[persistence] (
  override val assoc: PersistedAssoc[R],
  override protected val root: R
)
extends PersistentState[R] {

  def this(p: Persisted[R]) = this(p.assoc, p.orig)

  def map(f: R => R) = new Deleted(assoc, root)

}

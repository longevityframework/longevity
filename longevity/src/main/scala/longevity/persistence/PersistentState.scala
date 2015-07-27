package longevity.persistence

import emblem.TypeKey
import longevity.subdomain._

/** the persistent state of an aggregate */
sealed trait PersistentState[E <: RootEntity] {

  protected val e: E

  /** returns the aggregate */
  def get: E = e

  /** returns the persistent state of an entity modified according to function `f` */
  def map(f: E => E): PersistentState[E]

  /** returns an association to the aggregate */
  def assoc: Assoc[E]

}

/** the persistent state of an entity that hasn't been persisted yet. */
class Unpersisted[E <: RootEntity : TypeKey] private[persistence] (override protected val e: E)
extends PersistentState[E] {

  def map(f: E => E) = new Unpersisted(f(e))

  def assoc = Assoc(e)

}

/** the persistent state of a persisted entity */
class Persisted[E <: RootEntity] private[persistence] (
  val assoc: PersistedAssoc[E],
  private[persistence] val orig: E,
  protected val e: E
)
extends PersistentState[E] {

  private[persistence] def this(assoc: PersistedAssoc[E], e: E) = this(assoc, e, e)

  def map(f: E => E) = new Persisted(assoc, orig, f(e))

  // we may want to consider === here if we allow for non-case class entities
  def dirty = orig == e

}

/** the persistent state of a deleted entity */
class Deleted[E <: RootEntity] private[persistence] (
  override val assoc: PersistedAssoc[E],
  override protected val e: E
)
extends PersistentState[E] {

  def this(p: Persisted[E]) = this(p.assoc, p.orig)

  def map(f: E => E) = new Deleted(assoc, e)

}

package longevity.persistence

import longevity.subdomain.RootEntity

/** the persistent state of an aggregate */
sealed trait PersistentState[E <: RootEntity] {

  protected val e: E

  /** returns the persistent state of an entity modified according to function `f` */
  def map(f: E => E): PersistentState[E]

  /** returns the aggregate */
  def get: E = e

}

/** the persistent state of an entity that hasn't been persisted yet. */
case class Unpersisted[E <: RootEntity](e: E) extends PersistentState[E] {
  def map(f: E => E) = Unpersisted(f(e))
}

/** the persistent state of a persisted entity */
case class Persisted[E <: RootEntity](
  id: PersistedAssoc[E],
  orig: E,
  curr: E
)
extends PersistentState[E] {

  protected val e = curr

  def map(f: E => E) = Persisted(id, orig, f(curr))

  // we may want to consider === here if we allow for non-case class entities
  def dirty = orig == curr

}

object Persisted {
  def apply[E <: RootEntity](assoc: PersistedAssoc[E], e: E): Persisted[E] = Persisted[E](assoc, e, e)
}

/** the persistent state of a deleted entity */
case class Deleted[E <: RootEntity](
  id: PersistedAssoc[E],
  e: E
)
extends PersistentState[E] {
  def map(f: E => E) = Deleted(id, e)
}

object Deleted {
  def apply[E <: RootEntity](p: Persisted[E]): Deleted[E] = Deleted(p.id, p.orig)
}

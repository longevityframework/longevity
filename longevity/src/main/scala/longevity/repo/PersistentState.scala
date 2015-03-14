package longevity.repo

import longevity.domain.RootEntity

object PersistentState {

  @throws[PersistentState.PersistentStateIsNotPersisted[_]]
  implicit def persistentStateToPersisted[E <: RootEntity](state: PersistentState[E]): Persisted[E] =
    state.asPersisted

  // i would move this exception, but its probably going to go away in an upcoming story
  /** an attempt was made to cast a non-persisted persistent state into a persisted persistent state */
  class PersistentStateIsNotPersisted[E <: RootEntity](state: PersistentState[E])
  extends Exception(s"persistent state is not persisted: $state")

}

/** the persistence state of an aggregate. */
sealed trait PersistentState[E <: RootEntity] {

  /** updates the aggregate if non-empty. otherwise does nothing */
  def copy(f: E => E): PersistentState[E]

  /** iterates over the aggregate if non-empty. otherwise does nothing */
  def foreach(f: E => Unit): Unit

  /** true if there was an error for a persistence operation on the aggregate */
  def isError: Boolean

  /** returns the aggregate if non-error. otherwise returns `None`. */
  def getOption: Option[E]

  /** returns the aggregate */
  @throws[NoSuchElementException]
  def get: E = getOption.get

  @throws[PersistentState.PersistentStateIsNotPersisted[E]]
  def asPersisted: Persisted[E] = try {
    this.asInstanceOf[Persisted[E]]
  } catch {
    case _: ClassCastException => throw new PersistentState.PersistentStateIsNotPersisted(this)
  }

}

/** A non-error persistence state. */
sealed trait NonError[E <: RootEntity] extends PersistentState[E] {
  protected val e: E
  def foreach(f: E => Unit) = f(e)
  def isError = false
  def getOption = Some(e)
}

/** Any entity that hasn't been persisted yet. */
case class Unpersisted[E <: RootEntity](e: E) extends NonError[E] {
  def copy(f: E => E) = Unpersisted(f(e))
}

sealed trait CreateResult[E <: RootEntity] extends PersistentState[E]

sealed trait RetrieveResult[E <: RootEntity] extends PersistentState[E]

sealed trait UpdateResult[E <: RootEntity] extends PersistentState[E]

sealed trait DeleteResult[E <: RootEntity] extends PersistentState[E]

object Persisted {
  def apply[E <: RootEntity](assoc: PersistedAssoc[E], e: E): Persisted[E] = Persisted[E](assoc, e, e)
}

case class Persisted[E <: RootEntity](
  id: PersistedAssoc[E],
  orig: E,
  curr: E
)
extends NonError[E] with CreateResult[E] with RetrieveResult[E] with UpdateResult[E] {
  protected val e = curr
  def copy(f: E => E) = Persisted(id, orig, f(curr))
  def dirty = orig == curr
}

object Deleted {
  def apply[E <: RootEntity](p: Persisted[E]): Deleted[E] = Deleted(p.id, p.orig)
}

case class Deleted[E <: RootEntity](
  id: PersistedAssoc[E],
  e: E
)
extends NonError[E] with DeleteResult[E] {
  def copy(f: E => E) = Deleted(id, e)
}

trait Error[E <: RootEntity] extends PersistentState[E] {
  def copy(f: E => E) = this
  def foreach(f: E => Unit) = {}
  def isError = true
  def getOption = None
}

case class NotFound[E <: RootEntity](id: PersistedAssoc[E]) extends Error[E] with RetrieveResult[E]

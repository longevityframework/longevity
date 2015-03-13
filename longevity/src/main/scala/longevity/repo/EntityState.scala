package longevity.repo

import longevity.domain.RootEntity

// TODO: rename this file

object PersistentState {

  @throws[PersistentState.PersistentStateIsNotPersisted[_]]
  implicit def persistentStateToPersisted[E <: RootEntity](state: PersistentState[E]): Persisted[E] =
    state.asPersisted

  /** an attempt was made to cast a non-persisted persistent state into a persisted persistent state */
  class PersistentStateIsNotPersisted[E <: RootEntity](state: PersistentState[E])
  extends Exception(s"persistent state is not persisted: $state")

}

/** The persistence state of an entity. */
sealed trait PersistentState[E <: RootEntity] {

  /** updates the entity if non-empty. otherwise does nothing. */
  def copy(f: E => E): PersistentState[E]

  /** iterates over the entity if non-empty. otherwise does nothing. */
  def foreach(f: E => Unit): Unit

  /** true if there was an error for a persistence operation on the entity. */
  def isError: Boolean

  /** returns the entity if non-error. otherwise returns `None`. */
  def getOption: Option[E]

  /** returns the entity */
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
  def apply[E <: RootEntity](id: Id[E], e: E): Persisted[E] = Persisted[E](id, e, e)
}

case class Persisted[E <: RootEntity](
  id: Id[E],
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
  id: Id[E],
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

case class NotFound[E <: RootEntity](id: Id[E]) extends Error[E] with RetrieveResult[E]

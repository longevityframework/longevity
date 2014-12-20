package longevity.repo

import scala.language.implicitConversions
import longevity.domain.Entity

object EntityState {

  implicit def entityStateToPersisted[E <: Entity](rr: EntityState[E]): Persisted[E] = rr.asPersisted
}

/** The persistence state of an entity. */
sealed trait EntityState[E <: Entity] {

  /** updates the entity if non-empty. otherwise does nothing. */
  def copy(f: E => E): EntityState[E]

  /** iterates over the entity if non-empty. otherwise does nothing. */
  def foreach(f: E => Unit): Unit

  /** true if there was an error for a persistence operation on the entity. */
  def isError: Boolean

  /** returns the entity if non-error. otherwise returns `None`. */
  def getOption: Option[E]

  /** returns the entity */
  @throws[NoSuchElementException]
  def get: E = getOption.get

  // TODO: should throw customized error on failure
  def asPersisted: Persisted[E] = this.asInstanceOf[Persisted[E]]

}

/** A non-error persistence state. */
sealed trait NonError[E <: Entity] extends EntityState[E] {
  protected val e: E
  def foreach(f: E => Unit) = f(e)
  def isError = false
  def getOption = Some(e)
}

object Unpersisted {
  implicit def unpersisted[E <: Entity](e: E): Unpersisted[E] = Unpersisted(e)
}

/** Any entity that hasn't been persisted yet. */
case class Unpersisted[E <: Entity](e: E) extends NonError[E] {
  def copy(f: E => E) = Unpersisted(f(e))
}

sealed trait CreateResult[E <: Entity] extends EntityState[E]

sealed trait RetrieveResult[E <: Entity] extends EntityState[E]

sealed trait UpdateResult[E <: Entity] extends EntityState[E]

sealed trait DeleteResult[E <: Entity] extends EntityState[E]

object Persisted {

  def apply[E <: Entity](id: Id[E], e: E): Persisted[E] = Persisted[E](id, e, 0L)

  def apply[E <: Entity](id: Id[E], e: E, version: Long): Persisted[E] = Persisted[E](id, e, version, e, version)

}

case class Persisted[E <: Entity](
  id: Id[E], orig: E, origVersion: Long, curr: E, currVersion: Long
)
extends NonError[E] with CreateResult[E] with RetrieveResult[E] with UpdateResult[E] {
  protected val e = curr
  def copy(f: E => E) = Persisted(id, orig, origVersion, f(curr), origVersion + 1)
  def dirty = orig == curr
}

object Deleted {
  def apply[E <: Entity](p: Persisted[E]): Deleted[E] = Deleted(p.id, p.orig, p.origVersion, p.curr, p.currVersion)
}

case class Deleted[E <: Entity](
  id: Id[E], orig: E, origVersion: Long, curr: E, currVersion: Long
)
extends NonError[E] with DeleteResult[E] {
  protected val e = curr
  def copy(f: E => E) = Persisted(id, orig, origVersion, f(curr), origVersion + 1)
}

trait Error[E <: Entity] extends EntityState[E] {
  def copy(f: E => E) = this
  def foreach(f: E => Unit) = {}
  def isError = true
  def getOption = None
}

case class NotFound[E <: Entity](id: Id[E]) extends Error[E] with RetrieveResult[E]

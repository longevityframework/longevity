package longevity.repo

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import emblem._
import longevity.domain._
import longevity.context.BoundedContext

/** an in-memory repository for aggregate roots of type E */
class InMemRepo[E <: RootEntity : TypeKey](
  override val entityType: RootEntityType[E],
  protected val boundedContext: BoundedContext)
extends Repo[E] {
  repo =>

  protected[longevity] case class IntId(i: Int) extends PersistedAssoc[E] {
    val associateeTypeKey = repo.entityTypeKey
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this).map(_.get.get)
  }

  private var nextId = 0
  private var idToEntityMap = Map[PersistedAssoc[E], Persisted[E]]()

  def create(unpersisted: Unpersisted[E]) = getSessionCreationOrElse(unpersisted, {
    val id = synchronized {
      val id = IntId(nextId)
      nextId += 1
      id
    }
    patchUnpersistedAssocs(unpersisted.get).map(persist(id, _))
  })

  def retrieve(id: PersistedAssoc[E]) = Future { idToEntityMap.get(id) }

  def update(persisted: Persisted[E]) = patchUnpersistedAssocs(persisted.curr) map {
    persist(persisted.id, _)
  }

  def delete(persisted: Persisted[E]) = Future {
    synchronized { idToEntityMap -= persisted.id }
    Deleted(persisted)
  }

  private def persist(id: PersistedAssoc[E], e: E): Persisted[E] = {
    val persisted = Persisted[E](id, e)
    synchronized { idToEntityMap += (id -> persisted) }
    persisted
  }

}

package longevity.repo

import emblem._
import longevity.domain._

/** an in-memory repository for entities of type E */
class InMemRepo[E <: Entity : TypeKey](
  override val entityType: EntityType[E]
)(
  implicit repoPool: RepoPool
)
extends Repo[E](repoPool) {
  repo =>

  case class IntId(i: Int) extends Id[E] {
    val associateeTypeTag = repo.entityTypeKey.tag
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this)
  }

  private var nextId = 0
  private var idToEntityMap = Map[Id[E], Persisted[E]]()

  def create(unpersisted: Unpersisted[E]) = getSessionCreationOrElse(unpersisted, {
    val id = IntId(nextId)
    nextId += 1
    persist(id, patchUnpersistedAssocs(unpersisted.get))
  })

  def retrieve(id: Id[E]) = idToEntityMap.getOrElse(id, NotFound(id))

  def update(persisted: Persisted[E]) =
    persist(persisted.id, patchUnpersistedAssocs(persisted.curr))

  def delete(persisted: Persisted[E]) = {
    idToEntityMap -= persisted.id
    Deleted(persisted)
  }

  private def persist(id: Id[E], e: E): Persisted[E] = {
      val persisted = Persisted[E](id, e)
      idToEntityMap += (id -> persisted)
      persisted
  }

}

package longevity.repo

import emblem._
import longevity.domain._

/** an in-memory repository for aggregate roots of type E */
class InMemRepo[E <: RootEntity : TypeKey](override val entityType: RootEntityType[E]) extends Repo[E] {
  repo =>

  case class IntId(i: Int) extends PersistedAssoc[E] {
    val associateeTypeKey = repo.entityTypeKey
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this).get.get
  }

  private var nextId = 0
  private var idToEntityMap = Map[PersistedAssoc[E], Persisted[E]]()

  def create(unpersisted: Unpersisted[E]) = getSessionCreationOrElse(unpersisted, {
    val id = IntId(nextId)
    nextId += 1
    persist(id, patchUnpersistedAssocs(unpersisted.get))
  })

  def retrieve(id: PersistedAssoc[E]) = idToEntityMap.get(id)

  def update(persisted: Persisted[E]) = persist(persisted.id, patchUnpersistedAssocs(persisted.curr))

  def delete(persisted: Persisted[E]) = {
    idToEntityMap -= persisted.id
    Deleted(persisted)
  }

  private def persist(id: PersistedAssoc[E], e: E): Persisted[E] = {
    val persisted = Persisted[E](id, e)
    idToEntityMap += (id -> persisted)
    persisted
  }

}

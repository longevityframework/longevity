package longevity.persistence

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import emblem.imports._
import longevity.shorthands._
import longevity.subdomain._
import longevity.context.LongevityContext

/** an in-memory repository for aggregate roots of type `E`
 * 
 * @param entityType the entity type for the aggregate roots this repository handles
 * @param emblemPool a pool of emblems for the entities within the subdomain
 * @param shorthandPool a complete set of the shorthands used by the bounded context
 */
class InMemRepo[E <: RootEntity : TypeKey](
  entityType: RootEntityType[E],
  emblemPool: EmblemPool,
  shorthandPool: ShorthandPool)
extends Repo[E](
  entityType,
  emblemPool,
  shorthandPool) {
  repo =>

  private case class IntId(i: Int) extends PersistedAssoc[E] {
    val associateeTypeKey = repo.entityTypeKey
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this).map(_.get.get)
  }

  private var nextId = 0
  private var idToEntityMap = Map[PersistedAssoc[E], Persisted[E]]()

  def create(unpersisted: Unpersisted[E]) = getSessionCreationOrElse(unpersisted, {
    patchUnpersistedAssocs(unpersisted.get).map { e =>
      val id = repo.synchronized {
        val id = IntId(nextId)
        nextId += 1
        id
      }
      persist(id, e)
    }
  })

  def retrieve(assoc: PersistedAssoc[E]) = {
    val optionE = idToEntityMap.get(assoc)
    Promise.successful(optionE).future
  }

  def update(persisted: Persisted[E]) = patchUnpersistedAssocs(persisted.get) map {
    persist(persisted.assoc, _)
  }

  def delete(persisted: Persisted[E]) = {
    repo.synchronized { idToEntityMap -= persisted.assoc }
    val deleted = new Deleted(persisted)
    Promise.successful(deleted).future
  }

  private def persist(assoc: PersistedAssoc[E], e: E): Persisted[E] = {
    val persisted = new Persisted[E](assoc, e)
    repo.synchronized { idToEntityMap += (assoc -> persisted) }
    persisted
  }

}

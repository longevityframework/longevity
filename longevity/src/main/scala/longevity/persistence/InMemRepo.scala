package longevity.persistence

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import emblem.imports._
import longevity.exceptions.subdomain.AssocIsUnpersistedException
import longevity.subdomain._
import longevity.context.LongevityContext

/** an in-memory repository for aggregate roots of type `E`
 * 
 * @param entityType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 */
class InMemRepo[E <: RootEntity : TypeKey](
  entityType: RootEntityType[E],
  subdomain: Subdomain)
extends Repo[E](entityType, subdomain) {
  repo =>

  private case class IntId(i: Int) extends PersistedAssoc[E] {
    val associateeTypeKey = repo.entityTypeKey
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this).map(_.get)
  }

  private var nextId = 0
  private var idToEntityMap = Map[PersistedAssoc[E], Persisted[E]]()
  
  case class NKV(val key: Key[E], val keyVal: Key[E]#Val)
  private var nkvToEntityMap = Map[NKV, Persisted[E]]()

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

  def retrieve(key: Key[E])(keyVal: key.Val): Future[Option[Persisted[E]]] = {
    key.props.foreach { prop =>
      if (prop.typeKey <:< typeKey[Assoc[_]]) {
        val assoc = keyVal(prop).asInstanceOf[Assoc[_ <: RootEntity]]
        if (!assoc.isPersisted) throw new AssocIsUnpersistedException(assoc)
      }
    }
    val optionE = nkvToEntityMap.get(NKV(key, keyVal))
    Promise.successful(optionE).future
  }


  def update(persisted: Persisted[E]) = {
    dumpKeys(persisted.orig)
    patchUnpersistedAssocs(persisted.get) map {
      persist(persisted.assoc, _)
    }
  }

  def delete(persisted: Persisted[E]) = {
    repo.synchronized { idToEntityMap -= persisted.assoc }
    dumpKeys(persisted.orig)
    val deleted = new Deleted(persisted)
    Promise.successful(deleted).future
  }

  protected def retrieveByValidQuery(query: Query[E]): Future[Seq[Persisted[E]]] = {
    ???
  }

  private def retrieve(assoc: PersistedAssoc[E]) = {
    val optionE = idToEntityMap.get(assoc)
    Promise.successful(optionE).future
  }

  private def persist(assoc: PersistedAssoc[E], e: E): Persisted[E] = {
    val persisted = new Persisted[E](assoc, e)
    repo.synchronized {
      idToEntityMap += (assoc -> persisted)
      entityType.keys.foreach { key =>
        val keyVal = key.keyVal(e)
        nkvToEntityMap += (NKV(key, keyVal) -> persisted)
      }
    }
    persisted
  }

  private def dumpKeys(e: E) = repo.synchronized {
    entityType.keys.foreach { key =>
      val keyVal = key.keyVal(e)
      nkvToEntityMap -= NKV(key, keyVal)
    }
  }

}

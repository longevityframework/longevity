package longevity.repo

import emblem._
import longevity.domain._

/** a repository for entities of type E
 * @param repoPool the pool of all the repos in context
 */
abstract class Repo[E <: Entity : TypeKey](protected val repoPool: OldRepoPool) {

  /** the type key for the entities this repository handles */
  val entityTypeKey: TypeKey[E] = typeKey[E]

  /** the entity type for the entities this repository handles */
  val entityType: EntityType[E]

  repoPool.addRepo(this)

  /** creates the entity */
  def create(e: Unpersisted[E]): CreateResult[E]

  /** convenience method for creating the entity */
  def create(e: E): CreateResult[E] = create(Unpersisted(e))

  /** retrieves the entity by id */
  def retrieve(id: Id[E]): RetrieveResult[E]

  /** updates the entity */
  def update(p: Persisted[E]): UpdateResult[E]

  /** deletes the entity */
  def delete(p: Persisted[E]): DeleteResult[E]

  /** a cache of create results for those unpersisted entities of type E that have already been created.
   * because entities are just value objects, we expect some duplication in the unpersisted data that gets
   * passed into `Repo.create`, via the associations of created obects. we keep a session
   * level cache of these guys to prevent multiple creation attempts on the same entity.
   *
   * note that this cache does not stay current with any updates or deletes to these entities! this cache
   * is not intended for use with interleaving create/update/delete, but rather for a series of create calls.
   */
  protected var sessionCreations = Map[Unpersisted[E], CreateResult[E]]()

  /** pull a create result out of the cache for the given unpersisted. if it's not there, then create it,
   * cache it, and return it */
  protected def getSessionCreationOrElse(unpersisted: Unpersisted[E], create: => CreateResult[E])
  : CreateResult[E] = {
    val createResult = sessionCreations.getOrElse(unpersisted, {
      val createResult = create
      sessionCreations += (unpersisted -> createResult)
      createResult
    })
    createResult
  }

  /** returns a version of the entity where all unpersisted associations are persisted */
  protected def patchUnpersistedAssocs(e: E): E = {
    val e2 = entityType.assocProps.foldLeft(e) { (e, prop) =>
      prop.set(e, persistAssocWhenUnpersisted(prop.get(e)))
    }
    val e3 = entityType.assocSetProps.foldLeft(e2) { (e, prop) =>
      prop.set(e, prop.get(e) map { associatee => persistAssocWhenUnpersisted(associatee) })
    }
    val e4 = entityType.assocOptionProps.foldLeft(e3) { (e, prop) =>
      prop.set(e, prop.get(e) map { associatee => persistAssocWhenUnpersisted(associatee) })
    }
    e4
  }

  private def persistAssocWhenUnpersisted[Associatee <: Entity](assoc: Assoc[Associatee]): Assoc[Associatee] = {
    assoc match {
      case UnpersistedAssoc(u) =>
        val repo = repoPool.repoForEntityTypeKey(assoc.associateeTypeKey)
        val persisted = repo.create(u)
        persisted.id
      case _ => 
        assoc
    }
  }
  
}

package longevity.repo

import emblem._
import longevity.domain._

/** a repository for aggregate roots of type E
 * @param repoPool the pool of all the repos in context
 */
abstract class Repo[E <: RootEntity : TypeKey] {

  private[repo] var _repoPoolOption: Option[RepoPool] = None

  /** the type key for the aggregate roots this repository handles */
  val entityTypeKey: TypeKey[E] = typeKey[E]

  /** the entity type for the aggregate roots this repository handles */
  val entityType: EntityType[E]

  /** creates the aggregate */
  def create(e: Unpersisted[E]): Persisted[E]

  /** convenience method for creating the aggregate */
  def create(e: E): Persisted[E] = create(Unpersisted(e))

  /** retrieves the aggregate by id */
  def retrieve(id: PersistedAssoc[E]): Option[Persisted[E]]

  /** updates the aggregate */
  def update(p: Persisted[E]): Persisted[E]

  /** deletes the aggregate */
  def delete(p: Persisted[E]): Deleted[E]

  /** the pool of all the repos for the [[longevity.domain.BoundedContext bounded context]].
   *
   * PLEASE NOTE that the repo pool is only available for use after all the repositories in the pool have
   * been initialized. if you attempt to access the pool during the initialization of your customized
   * repository, you will get a NoSuchElementException.
   */
  protected lazy val repoPool: RepoPool = _repoPoolOption.get

  /** a cache of create results for those unpersisted entities of type E that have already been created.
   * because entities are just value objects, we expect some duplication in the unpersisted data that gets
   * passed into `Repo.create`, via the associations of created obects. we keep a session
   * level cache of these guys to prevent multiple creation attempts on the same aggregate.
   *
   * note that this cache does not stay current with any updates or deletes to these entities! this cache
   * is not intended for use with interleaving create/update/delete, but rather for a series of create calls.
   */
  protected var sessionCreations = Map[Unpersisted[E], Persisted[E]]()

  /** pull a create result out of the cache for the given unpersisted. if it's not there, then create it,
   * cache it, and return it */
  protected def getSessionCreationOrElse(unpersisted: Unpersisted[E], create: => Persisted[E])
  : Persisted[E] = {
    val persisted = sessionCreations.getOrElse(unpersisted, {
      val persisted = create
      sessionCreations += (unpersisted -> persisted)
      persisted
    })
    persisted
  }

  /** returns a version of the aggregate where all unpersisted associations are persisted */
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

  private def persistAssocWhenUnpersisted[
    Associatee <: RootEntity](
    assoc: Assoc[Associatee])
  : Assoc[Associatee] = {
    assoc match {
      case UnpersistedAssoc(u) =>
        val repo = repoPool(assoc.associateeTypeKey)
        val persisted = repo.create(u)
        persisted.id
      case _ => 
        assoc
    }
  }
  
}

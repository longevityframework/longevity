package longevity.repo

import scala.reflect.runtime.universe.TypeTag

import longevity.domain._

trait Repo[E <: Entity] {

  /** the class tag for the entities this repository handles */
  val entityTypeTag: TypeTag[E]

  /** the entity type for the entities this repository handles */
  val entityType: EntityType[E]

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

  /** the pool of all the repos in context */
  protected val repoPool: RepoPool

  repoPool.addRepo(this)

  /** a cache of create results for those unpersisted entities of type E that have already been created.
   * because entities are just value objects, we expect some duplication in the unpersisted data that gets
   * passed into [[Repo.create(Unpersisted[E])]], via the associations of created obects. we keep a session
   * level cache of these guys to prevent multiple creation attempts on the same entity.
   *
   * note that this cache does not stay current with any updates or deletes to these entities! this cache
   * is not intended for use with interleaving create/update/delete, but rather for a series of create calls. */
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
    entityType.assocLenses.foldLeft(e) { (e, lens) =>
      patchUnpersistedAssoc(e, lens)
    }
  }

  private def patchUnpersistedAssoc[F <: Entity](e: E, lens: EntityType.AssocLens[E, F]): E = {
    implicit val associateeTypeTag: TypeTag[F] = lens.associateeTypeTag
    lens.patchAssoc(e, persistAssocWhenUnpersisted)
  }

  private def persistAssocWhenUnpersisted[Associatee <: Entity](assoc: Assoc[Associatee])(
    implicit associateeTypeTag: TypeTag[Associatee]
  ): Assoc[Associatee] = {
    assoc match {
      case UnpersistedAssoc(u) =>
        val repo = repoPool.repoForEntityTypeTag(associateeTypeTag)
        val persisted = repo.create(u)
        persisted.id
      case _ => assoc
    }
  }
  
}

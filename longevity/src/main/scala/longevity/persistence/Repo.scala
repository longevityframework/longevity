package longevity.persistence

import emblem.imports._
import longevity.shorthands._
import longevity.subdomain._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/** a repository for aggregate roots of type `E`.
 * 
 * @param entityType the entity type for the aggregate roots this repository handles
 * @param emblemPool a pool of emblems for the entities within the subdomain
 * @param shorthandPool a complete set of the shorthands used by the bounded context
 */
abstract class Repo[E <: RootEntity : TypeKey](
  val entityType: EntityType[E],
  emblemPool: EmblemPool,
  shorthandPool: ShorthandPool) {

  private[persistence] var _repoPoolOption: Option[RepoPool] = None

  /** the type key for the aggregate roots this repository handles */
  val entityTypeKey: TypeKey[E] = typeKey[E]

  /** creates the aggregate */
  def create(e: Unpersisted[E]): Future[Persisted[E]]

  /** convenience method for creating the aggregate */
  def create(e: E): Future[Persisted[E]] = create(new Unpersisted(e))

  /** retrieves the aggregate by id */
  def retrieve(id: PersistedAssoc[E]): Future[Option[Persisted[E]]]

  /** updates the aggregate */
  def update(p: Persisted[E]): Future[Persisted[E]]

  /** deletes the aggregate */
  def delete(p: Persisted[E]): Future[Deleted[E]]

  /** the pool of all the repos for the [[longevity.context.PersistenceContext]] */
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
  protected def getSessionCreationOrElse(unpersisted: Unpersisted[E], create: => Future[Persisted[E]])
  : Future[Persisted[E]] = {
    sessionCreations.get(unpersisted).map(Promise.successful(_).future).getOrElse {
      create.map { persisted =>
        sessionCreations += (unpersisted -> persisted)
        persisted
      }
    }
  }

  private lazy val extractorPool = shorthandPoolToExtractorPool(shorthandPool)

  private lazy val unpersistedToPersistedTransformer =
    new UnpersistedToPersistedTransformer(repoPool, emblemPool, extractorPool)

  /** returns a version of the aggregate where all unpersisted associations are persisted */
  protected def patchUnpersistedAssocs(entity: E): Future[E] = {
    val futureE = Promise.successful[E](entity).future
    unpersistedToPersistedTransformer.transform(futureE)
  }
  
}

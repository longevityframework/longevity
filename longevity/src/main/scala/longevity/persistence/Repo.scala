package longevity.persistence

import emblem.imports._
import longevity.subdomain._
import longevity.subdomain.root._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/** a repository for aggregate roots of type `R`.
 * 
 * @param entityType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 */
abstract class Repo[R <: RootEntity : TypeKey](
  val entityType: RootEntityType[R],
  val subdomain: Subdomain) {

  private[persistence] var _repoPoolOption: Option[RepoPool] = None

  /** contains implicit imports to make the query DSL work */
  lazy val queryDsl = new QueryDsl[R]

  /** the type key for the aggregate roots this repository handles */
  val entityTypeKey: TypeKey[R] = typeKey[R]

  /** creates the aggregate */
  def create(state: Unpersisted[R]): Future[Persisted[R]]

  /** convenience method for creating the aggregate */
  def create(root: R): Future[Persisted[R]] = create(new Unpersisted(root))

  /** retrieves the aggregate by a key value */
  def retrieve(key: Key[R])(keyVal: key.Val): Future[Option[Persisted[R]]]

  /** retrieves the aggregate by a query */
  def retrieveByQuery(query: Query[R]): Future[Seq[Persisted[R]]] =
    retrieveByValidatedQuery(entityType.validateQuery(query))

  /** updates the aggregate */
  def update(p: Persisted[R]): Future[Persisted[R]]

  /** deletes the aggregate */
  def delete(p: Persisted[R]): Future[Deleted[R]]

  protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[Persisted[R]]]

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
  protected var sessionCreations = Map[Unpersisted[R], Persisted[R]]()

  /** pull a create result out of the cache for the given unpersisted. if it's not there, then create it,
   * cache it, and return it */
  protected def getSessionCreationOrElse(unpersisted: Unpersisted[R], create: => Future[Persisted[R]])
  : Future[Persisted[R]] = {
    sessionCreations.get(unpersisted).map(Promise.successful(_).future).getOrElse {
      create.map { persisted =>
        sessionCreations += (unpersisted -> persisted)
        persisted
      }
    }
  }

  private lazy val extractorPool = shorthandPoolToExtractorPool(subdomain.shorthandPool)

  private lazy val unpersistedToPersistedTransformer =
    new UnpersistedToPersistedTransformer(repoPool, subdomain.entityEmblemPool, extractorPool)

  /** returns a version of the aggregate where all unpersisted associations are persisted */
  protected def patchUnpersistedAssocs(root: R): Future[R] = {
    val futureRoot = Promise.successful[R](root).future
    unpersistedToPersistedTransformer.transform(futureRoot)
  }
  
}

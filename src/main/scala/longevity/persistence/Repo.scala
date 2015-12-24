package longevity.persistence

import emblem.imports._
import longevity.subdomain._
import longevity.subdomain.root._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/** a repository for aggregate roots of type `R`.
 * 
 * @param rootType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 */
abstract class Repo[R <: Root : TypeKey] private[persistence] (
  protected[longevity] val rootType: RootType[R],
  protected[longevity] val subdomain: Subdomain) {

  private[persistence] var _repoPoolOption: Option[RepoPool] = None

  /** contains implicit imports to make the query DSL work */
  lazy val queryDsl = new QueryDsl[R]

  /** the type key for the aggregate roots this repository handles */
  protected val rootTypeKey: TypeKey[R] = typeKey[R]

  /** creates the aggregate */
  def create(unpersisted: R): Future[PState[R]]

  /** retrieves the aggregate by a key value */
  def retrieve(keyValForRoot: KeyVal[R]): Future[Option[PState[R]]]

  /** retrieves the aggregate by a query */
  def retrieveByQuery(query: Query[R]): Future[Seq[PState[R]]] =
    retrieveByValidatedQuery(rootType.validateQuery(query))

  /** updates the aggregate */
  def update(state: PState[R]): Future[PState[R]]

  /** deletes the aggregate */
  def delete(state: PState[R]): Future[Deleted[R]]

  protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[PState[R]]]

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
  protected var sessionCreations = Map[R, PState[R]]()

  /** pull a create result out of the cache for the given unpersisted. if it's not there, then create it,
   * cache it, and return it */
  protected def getSessionCreationOrElse(unpersisted: R, create: => Future[PState[R]])
  : Future[PState[R]] = {
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

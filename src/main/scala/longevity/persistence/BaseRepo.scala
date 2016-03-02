package longevity.persistence

import emblem.imports._
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.subdomain._
import longevity.subdomain.root._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/** an abstract base class for [[Repo]] implementations.
 * 
 * @param rootType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 */
private[longevity] abstract class BaseRepo[R <: Root : TypeKey] private[persistence] (
  protected[longevity] val rootType: RootType[R],
  protected[longevity] val subdomain: Subdomain)
extends Repo[R] {

  private[persistence] var _repoPoolOption: Option[RepoPool] = None

  /** the type key for the aggregate roots this repository handles */
  protected val rootTypeKey: TypeKey[R] = typeKey[R]

  def create(unpersisted: R): Future[PState[R]]

  def retrieve(ref: PRef[R]): Future[Option[PState[R]]] = ref.pattern match {
    case PRef.UAssocPattern(assoc) => throw new AssocIsUnpersistedException(assoc)
    case PRef.PAssocPattern(assoc) => retrieveByPersistedAssoc(assoc)
    case PRef.KeyValPattern(keyVal) => retrieveByKeyVal(keyVal)
  }

  def retrieveOne(ref: PRef[R]): Future[PState[R]] = retrieve(ref).map(_.get)

  def retrieveByQuery(query: Query[R]): Future[Seq[PState[R]]] =
    retrieveByValidatedQuery(rootType.validateQuery(query))

  def update(state: PState[R]): Future[PState[R]]

  def delete(state: PState[R]): Future[Deleted[R]]

  protected def retrieveByPersistedAssoc(assoc: PersistedAssoc[R]): Future[Option[PState[R]]]

  protected def retrieveByKeyVal(keyVal: KeyVal[R]): Future[Option[PState[R]]]

  protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[PState[R]]]

  /** the pool of all the repos for the [[longevity.context.PersistenceContext]] */
  protected lazy val repoPool: RepoPool = _repoPoolOption.get

  private[persistence] def createWithCache(unpersisted: R, cache: CreatedCache)
  : Future[(PState[R], CreatedCache)] = {
    cache.get[R](unpersisted) match {
      case Some(pstate) => Future.successful((pstate, cache))
      case None => patchUnpersistedAssocs(unpersisted, cache).flatMap {
        case (patched, cache) => create(patched).map {
          pstate => (pstate, cache + (unpersisted -> pstate))
        }
      }
    }
  }

  private lazy val extractorPool = shorthandPoolToExtractorPool(subdomain.shorthandPool)

  // this is also used by RepoCrudSpec for making pretty test data
  private[longevity] def patchUnpersistedAssocs(root: R, cache: CreatedCache): Future[(R, CreatedCache)] = {
    val unpersistedToPersistedTransformer =
      new UnpersistedToPersistedTransformer(repoPool, subdomain.entityEmblemPool, extractorPool, cache)
    implicit val rootTypeTag = rootTypeKey.tag
    val futureRoot = Future.successful(root)
    val futurePatched = unpersistedToPersistedTransformer.transform(futureRoot)
    futurePatched.map((_, unpersistedToPersistedTransformer.createdCache))
  }

}

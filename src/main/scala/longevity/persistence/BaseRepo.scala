package longevity.persistence

import emblem.imports._
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.subdomain._
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype._
import scala.concurrent._

/** an abstract base class for [[Repo]] implementations.
 * 
 * @param pType the entity type for the persistent entities this repository handles
 * @param subdomain the subdomain containing the persistent entities that this repo persists
 */
private[longevity] abstract class BaseRepo[P <: Persistent : TypeKey] private[persistence] (
  protected[longevity] val pType: PType[P],
  protected[longevity] val subdomain: Subdomain)
extends Repo[P] {

  private[persistence] var _repoPoolOption: Option[RepoPool] = None

  /** the type key for the persistent entities this repository handles */
  protected val pTypeKey: TypeKey[P] = typeKey[P]

  def create(unpersisted: P)(implicit context: ExecutionContext): Future[PState[P]]

  def retrieve(ref: PRef[P])(implicit context: ExecutionContext): Future[Option[PState[P]]] =
    ref.pattern match {
      case PRef.UAssocPattern(assoc) => throw new AssocIsUnpersistedException(assoc)
      case PRef.PAssocPattern(assoc) => retrieveByPersistedAssoc(assoc)
      case PRef.KeyValPattern(keyVal) => retrieveByKeyVal(keyVal)
    }

  def retrieveOne(ref: PRef[P])(implicit context: ExecutionContext): Future[PState[P]] =
    retrieve(ref).map(_.get)

  def update(state: PState[P])(implicit context: ExecutionContext): Future[PState[P]]

  def delete(state: PState[P])(implicit context: ExecutionContext): Future[Deleted[P]]

  protected def retrieveByPersistedAssoc(assoc: PersistedAssoc[P])(implicit context: ExecutionContext)
  : Future[Option[PState[P]]]

  protected def retrieveByKeyVal(keyVal: KeyVal[P])(implicit context: ExecutionContext)
  : Future[Option[PState[P]]]

  /** the pool of all the repos for the [[longevity.context.PersistenceContext]] */
  protected lazy val repoPool: RepoPool = _repoPoolOption.get

  private[persistence] def createWithCache(
    unpersisted: P,
    cache: CreatedCache)(
    implicit executionContext: ExecutionContext)
  : Future[(PState[P], CreatedCache)] = {
    cache.get[P](unpersisted) match {
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
  private[longevity] def patchUnpersistedAssocs(
    p: P,
    cache: CreatedCache)(
    implicit executionContext: ExecutionContext)
  : Future[(P, CreatedCache)] = {
    val transformer = new UnpersistedToPersistedTransformer(
      repoPool,
      executionContext,
      subdomain.entityEmblemPool,
      extractorPool,
      cache)
    implicit val pTypeTag = pTypeKey.tag
    val futureP = Future.successful(p)
    val futurePatched = transformer.transform(futureP)
    futurePatched.map((_, transformer.createdCache))
  }

}

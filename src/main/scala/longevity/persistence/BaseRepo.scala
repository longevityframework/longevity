package longevity.persistence

import emblem.TypeKey
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.KeyVal
import longevity.subdomain.ptype.PType
import longevity.subdomain.realized.RealizedPType
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** an abstract base class for [[Repo]] implementations
 * 
 * @param pType the entity type for the persistent entities this repository handles
 * @param subdomain the subdomain containing the persistent entities that this repo persists
 */
private[longevity] abstract class BaseRepo[P <: Persistent] private[persistence] (
  protected[longevity] val pType: PType[P],
  protected[longevity] val subdomain: Subdomain)
extends Repo[P] {

  private[persistence] var _repoPoolOption: Option[RepoPool] = None

  /** the pool of all the repos for the [[longevity.context.PersistenceContext]] */
  protected lazy val repoPool: RepoPool = _repoPoolOption.get

  protected[longevity] val realizedPType: RealizedPType[P] = subdomain.realizedPTypes(pType)

  /** the type key for the persistent entities this repository handles */
  protected[persistence] val pTypeKey: TypeKey[P] = pType.pTypeKey

  def create(unpersisted: P)(implicit context: ExecutionContext): Future[PState[P]]

  def retrieve[V <: KeyVal[P, V]](keyVal: V)(implicit context: ExecutionContext): Future[Option[PState[P]]]

  def retrieveOne[V <: KeyVal[P, V]](keyVal: V)(implicit context: ExecutionContext): Future[PState[P]] =
    retrieve(keyVal).map(_.get)

  def update(state: PState[P])(implicit context: ExecutionContext): Future[PState[P]]

  def delete(state: PState[P])(implicit context: ExecutionContext): Future[Deleted[P]]

}

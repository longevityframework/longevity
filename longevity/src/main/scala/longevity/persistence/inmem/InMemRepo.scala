package longevity.persistence.inmem

import com.typesafe.scalalogging.LazyLogging
import longevity.context.PersistenceConfig
import longevity.persistence.BaseRepo
import longevity.persistence.DatabaseId
import longevity.persistence.PState
import longevity.subdomain.DerivedPType
import longevity.subdomain.KeyVal
import longevity.subdomain.PType
import longevity.subdomain.PolyPType
import longevity.subdomain.Subdomain
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** an in-memory repository for persistent entities of type `P`
 * 
 * @param pType the persistent type for the entities this repository handles
 * @param subdomain the subdomain containing the entities that this repo persists
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class InMemRepo[P] private[persistence] (
  pType: PType[P],
  subdomain: Subdomain,
  protected val persistenceConfig: PersistenceConfig)
extends BaseRepo[P](pType, subdomain)
with InMemCreate[P]
with InMemDelete[P]
with InMemQuery[P]
with InMemRead[P]
with InMemRetrieve[P]
with InMemUpdate[P]
with InMemWrite[P]
with LazyLogging {
  repo =>

  // i wish i could type this tighter, but compiler is giving me problems..
  protected type AnyKeyValAtAll = KeyVal[P] forSome { type P }

  protected var idToPStateMap = Map[DatabaseId[_], PState[P]]()
  protected var keyValToPStateMap = Map[AnyKeyValAtAll, PState[P]]()

 protected[persistence] def close()(implicit context: ExecutionContext): Future[Unit] =
    Future.successful(())

  protected[persistence] def createSchema()(implicit context: ExecutionContext): Future[Unit] =
    Future.successful(())

  override def toString = s"InMemRepo[${pTypeKey.name}]"

}

private[longevity] object InMemRepo {

  private[persistence] def apply[P](
    pType: PType[P],
    subdomain: Subdomain,
    persistenceConfig: PersistenceConfig,
    polyRepoOpt: Option[InMemRepo[_ >: P]])
  : InMemRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new InMemRepo(pType, subdomain, persistenceConfig) with PolyInMemRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P](poly: InMemRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: InMemRepo[Poly] = poly
          }
          with InMemRepo(pType, subdomain, persistenceConfig) with DerivedInMemRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new InMemRepo(pType, subdomain, persistenceConfig)
    }
    repo
  }

}

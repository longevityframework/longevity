package longevity.persistence.inmem

import com.typesafe.scalalogging.LazyLogging
import longevity.config.PersistenceConfig
import longevity.persistence.PRepo
import longevity.persistence.DatabaseId
import longevity.persistence.PState
import longevity.model.DerivedPType
import longevity.model.KeyVal
import longevity.model.PType
import longevity.model.PolyPType
import longevity.model.ModelType
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** an in-memory repository for persistent entities of type `P`
 * 
 * @param pType the persistent type for the entities this repository handles
 * @param modelType the model type containing the entities that this repo persists
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class InMemRepo[P] private[persistence] (
  pType: PType[P],
  modelType: ModelType,
  protected val persistenceConfig: PersistenceConfig)
extends PRepo[P](pType, modelType)
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
    modelType: ModelType,
    persistenceConfig: PersistenceConfig,
    polyRepoOpt: Option[InMemRepo[_ >: P]])
  : InMemRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new InMemRepo(pType, modelType, persistenceConfig) with PolyInMemRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P](poly: InMemRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: InMemRepo[Poly] = poly
          }
          with InMemRepo(pType, modelType, persistenceConfig) with DerivedInMemRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new InMemRepo(pType, modelType, persistenceConfig)
    }
    repo
  }

}

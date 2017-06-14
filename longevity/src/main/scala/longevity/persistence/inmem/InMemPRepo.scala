package longevity.persistence.inmem

import com.typesafe.scalalogging.LazyLogging
import longevity.config.PersistenceConfig
import longevity.persistence.PRepo
import longevity.persistence.DatabaseId
import longevity.persistence.PState
import longevity.model.DerivedPType
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
private[longevity] class InMemPRepo[M, P] private[persistence] (
  pType: PType[M, P],
  modelType: ModelType[M],
  protected val persistenceConfig: PersistenceConfig)
extends PRepo[M, P](pType, modelType)
with InMemCreate[M, P]
with InMemDelete[M, P]
with InMemQuery[M, P]
with InMemRead[M, P]
with InMemRetrieve[M, P]
with InMemUpdate[M, P]
with InMemWrite[M, P]
with LazyLogging {
  repo =>

  protected var idToPStateMap = Map[DatabaseId[_], PState[P]]()
  protected var keyValToPStateMap = Map[Any, PState[P]]()

 protected[persistence] def close()(implicit context: ExecutionContext): Future[Unit] =
    Future.successful(())

  protected[persistence] def createSchemaBlocking(): Unit = ()

  override def toString = s"InMemPRepo[${pTypeKey.name}]"

}

private[longevity] object InMemPRepo {

  private[persistence] def apply[M, P](
    pType: PType[M, P],
    modelType: ModelType[M],
    persistenceConfig: PersistenceConfig,
    polyRepoOpt: Option[InMemPRepo[M, _ >: P]])
  : InMemPRepo[M, P] = {
    val repo = pType match {
      case pt: PolyPType[_, _] =>
        new InMemPRepo(pType, modelType, persistenceConfig) with PolyInMemPRepo[M, P]
      case pt: DerivedPType[_, _, _] =>
        def withPoly[Poly >: P](poly: InMemPRepo[M, Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: InMemPRepo[M, Poly] = poly
          }
          with InMemPRepo(pType, modelType, persistenceConfig) with DerivedInMemPRepo[M, P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new InMemPRepo(pType, modelType, persistenceConfig)
    }
    repo
  }

}

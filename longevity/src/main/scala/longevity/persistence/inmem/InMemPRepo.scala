package longevity.persistence.inmem

import com.typesafe.scalalogging.LazyLogging
import longevity.config.PersistenceConfig
import longevity.effect.Effect
import longevity.persistence.PRepo
import longevity.persistence.DatabaseId
import longevity.persistence.PState
import longevity.model.DerivedPType
import longevity.model.PType
import longevity.model.PolyPType
import longevity.model.ModelType

/** an in-memory repository for persistent entities of type `P` */
private[longevity] class InMemPRepo[F[_], M, P] private[persistence] (
  effect: Effect[F],
  modelType: ModelType[M],
  pType: PType[M, P],
  protected val persistenceConfig: PersistenceConfig)
extends PRepo[F, M, P](effect, modelType, pType)
with InMemCreate[F, M, P]
with InMemDelete[F, M, P]
with InMemQuery[F, M, P]
with InMemRead[F, M, P]
with InMemRetrieve[F, M, P]
with InMemUpdate[F, M, P]
with InMemWrite[F, M, P]
with LazyLogging {
  repo =>

  protected var idToPStateMap = Map[DatabaseId[_], PState[P]]()
  protected var keyValToPStateMap = Map[Any, PState[P]]()

  protected[persistence] def createSchemaBlocking(): Unit = ()

  override def toString = s"InMemPRepo[${pTypeKey.name}]"

}

private[longevity] object InMemPRepo {

  private[persistence] def apply[F[_], M, P](
    effect: Effect[F],
    modelType: ModelType[M],
    pType: PType[M, P],
    persistenceConfig: PersistenceConfig,
    polyRepoOpt: Option[InMemPRepo[F, M, _ >: P]])
  : InMemPRepo[F, M, P] = {
    val repo = pType match {
      case pt: PolyPType[_, _] =>
        new InMemPRepo(effect, modelType, pType, persistenceConfig) with PolyInMemPRepo[F, M, P]
      case pt: DerivedPType[_, _, _] =>
        def withPoly[Poly >: P](poly: InMemPRepo[F, M, Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: InMemPRepo[F, M, Poly] = poly
          }
          with InMemPRepo(effect, modelType, pType, persistenceConfig) with DerivedInMemPRepo[F, M, P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new InMemPRepo(effect, modelType, pType, persistenceConfig)
    }
    repo
  }

}

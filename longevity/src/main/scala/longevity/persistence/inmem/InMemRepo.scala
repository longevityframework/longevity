package longevity.persistence.inmem

import longevity.config.PersistenceConfig
import longevity.effect.Effect
import longevity.model.ModelType
import longevity.model.PType
import longevity.persistence.Repo

private[persistence] class InMemRepo[F[_], M] private[persistence](
  effect: Effect[F],
  modelType: ModelType[M],
  persistenceConfig: PersistenceConfig)
extends Repo[F, M](effect, modelType, persistenceConfig) {

  type R[P] = InMemPRepo[F, M, P]

  protected def buildPRepo[P](pType: PType[M, P], polyRepoOpt: Option[R[_ >: P]] = None): R[P] =
    InMemPRepo[F, M, P](effect, modelType, pType, persistenceConfig, polyRepoOpt)

  protected def openBaseConnectionBlocking(): Unit = ()

  protected def createBaseSchemaBlocking(): Unit = ()

  protected def closeConnectionBlocking(): Unit = ()

}

package longevity.persistence.inmem

import longevity.config.PersistenceConfig
import longevity.model.ModelType
import longevity.model.PType
import longevity.persistence.Repo

private[persistence] class InMemRepo[M] private[persistence](
  modelType: ModelType[M],
  persistenceConfig: PersistenceConfig)
extends Repo[M](modelType, persistenceConfig) {

  type R[P] = InMemPRepo[M, P]

  protected def buildPRepo[P](pType: PType[M, P], polyRepoOpt: Option[R[_ >: P]] = None): R[P] =
    InMemPRepo[M, P](pType, modelType, persistenceConfig, polyRepoOpt)

  protected def openBaseConnectionBlocking(): Unit = ()

  protected def createBaseSchemaBlocking(): Unit = ()

  protected def closeConnectionBlocking(): Unit = ()

}

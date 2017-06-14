package longevity.persistence.jdbc

import longevity.config.JdbcConfig
import longevity.config.PersistenceConfig
import longevity.model.ModelType
import longevity.model.PType

private[persistence] class JdbcRepo[M] private[persistence](
  modelType: ModelType[M],
  persistenceConfig: PersistenceConfig,
  jdbcConfig: JdbcConfig)
extends BaseJdbcRepo[M](modelType, persistenceConfig, jdbcConfig) {

  type R[P] = JdbcPRepo[M, P]

  protected def buildPRepo[P](pType: PType[M, P], polyRepoOpt: Option[R[_ >: P]] = None): R[P] =
    JdbcPRepo[M, P](pType, modelType, persistenceConfig, polyRepoOpt, connection)

}

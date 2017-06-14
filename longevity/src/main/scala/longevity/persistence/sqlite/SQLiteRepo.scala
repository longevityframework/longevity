package longevity.persistence.sqlite

import longevity.config.JdbcConfig
import longevity.config.PersistenceConfig
import longevity.model.ModelType
import longevity.model.PType
import longevity.persistence.jdbc.BaseJdbcRepo

private[persistence] class SQLiteRepo[M] private[persistence](
  modelType: ModelType[M],
  persistenceConfig: PersistenceConfig,
  jdbcConfig: JdbcConfig)
extends BaseJdbcRepo[M](modelType, persistenceConfig, jdbcConfig) {

  type R[P] = SQLitePRepo[M, P]

  protected def buildPRepo[P](pType: PType[M, P], polyRepoOpt: Option[R[_ >: P]] = None): R[P] =
    SQLitePRepo[M, P](pType, modelType, persistenceConfig, polyRepoOpt, connection)

}

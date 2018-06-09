package longevity.persistence.sqlite

import longevity.config.JdbcConfig
import longevity.config.PersistenceConfig
import longevity.effect.Effect
import longevity.model.ModelType
import longevity.model.PType
import longevity.persistence.jdbc.BaseJdbcRepo

private[persistence] class SQLiteRepo[F[_], M] private[persistence](
  effect: Effect[F],
  modelType: ModelType[M],
  persistenceConfig: PersistenceConfig,
  jdbcConfig: JdbcConfig)
extends BaseJdbcRepo[F, M](effect, modelType, persistenceConfig, jdbcConfig) {

  type R[P] = SQLitePRepo[F, M, P]

  protected def buildPRepo[P](pType: PType[M, P], polyRepoOpt: Option[R[_ >: P]] = None): R[P] =
    SQLitePRepo[F, M, P](effect, modelType, pType, persistenceConfig, polyRepoOpt, wrappedConnection)

}

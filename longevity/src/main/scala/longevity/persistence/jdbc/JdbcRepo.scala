package longevity.persistence.jdbc

import longevity.config.JdbcConfig
import longevity.config.PersistenceConfig
import longevity.effect.Effect
import longevity.model.ModelType
import longevity.model.PType

private[persistence] class JdbcRepo[F[_], M] private[persistence](
  effect: Effect[F],
  modelType: ModelType[M],
  persistenceConfig: PersistenceConfig,
  jdbcConfig: JdbcConfig)
extends BaseJdbcRepo[F, M](effect, modelType, persistenceConfig, jdbcConfig) {

  type R[P] = JdbcPRepo[F, M, P]

  protected def buildPRepo[P](pType: PType[M, P], polyRepoOpt: Option[R[_ >: P]] = None): R[P] =
    JdbcPRepo[F, M, P](effect, modelType, pType, persistenceConfig, polyRepoOpt, wrappedConnection)

}

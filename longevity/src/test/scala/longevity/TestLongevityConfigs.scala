package longevity

import longevity.config.Cassandra
import longevity.config.InMem
import longevity.config.LongevityConfig
import longevity.config.MongoDB
import longevity.config.SQLite
import longevity.context.Effect
import longevity.context.LongevityContext
import longevity.model.ModelType
import longevity.test.CustomGeneratorPool

object TestLongevityConfigs {

  val baseConfig = LongevityConfig.fromTypesafeConfig()

  def configForKey(key: ConfigMatrixKey) = baseConfig.copy(
    backEnd = key.backEnd,
    autoOpenConnection = true,
    autoCreateSchema = key.autoCreateSchema,
    optimisticLocking = key.optimisticLocking,
    writeTimestamps = key.writeTimestamps,
    test = baseConfig.test.copy(
      cassandra = baseConfig.test.cassandra.copy(autoCreateKeyspace = true)))

  val configMatrix = ConfigMatrixKey.values.map(key => key -> configForKey(key)).toMap

  val sparseConfigMatrix = ConfigMatrixKey.sparseValues.map(key => key -> configForKey(key)).toMap

  def contexts[F[_] : Effect, M : ModelType](
    configKeys: Seq[ConfigMatrixKey],
    generators: CustomGeneratorPool = CustomGeneratorPool.empty)
  : Seq[LongevityContext[F, M]] =
    configKeys.map { key =>
      new LongevityContext(configForKey(key), generators)
    }

  def contextMatrix[F[_] : Effect, M : ModelType](
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext[F, M]] =
    contexts(ConfigMatrixKey.values, generators)

  def sparseContextMatrix[F[_] : Effect, M : ModelType](
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext[F, M]] =
    contexts(ConfigMatrixKey.sparseValues, generators)

  def cassandraOnlyContextMatrix[F[_] : Effect, M : ModelType](
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext[F, M]] =
    contexts(Seq(cassandraConfigKey), generators)

  def mongoOnlyContextMatrix[F[_] : Effect, M : ModelType](
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext[F, M]] =
    contexts(Seq(mongoConfigKey), generators)

  def sqliteOnlyContextMatrix[F[_] : Effect, M : ModelType](
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext[F, M]] =
    contexts(Seq(sqliteConfigKey), generators)

  val inMemConfigKey     = ConfigMatrixKey(InMem,     false, false, false)
  val cassandraConfigKey = ConfigMatrixKey(Cassandra, false, false, false)
  val mongoConfigKey     = ConfigMatrixKey(MongoDB,   false, false, false)
  val sqliteConfigKey    = ConfigMatrixKey(SQLite,    false, false, false)

  val inMemConfig     = configMatrix(inMemConfigKey)
  val cassandraConfig = configMatrix(cassandraConfigKey)
  val mongoConfig     = configMatrix(mongoConfigKey)
  val sqliteConfig    = configMatrix(sqliteConfigKey)

}

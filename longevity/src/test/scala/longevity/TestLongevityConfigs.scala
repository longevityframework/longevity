package longevity

import longevity.config.Cassandra
import longevity.config.InMem
import longevity.config.LongevityConfig
import longevity.config.MongoDB
import longevity.config.SQLite
import longevity.context.LongevityContext
import longevity.model.ModelType
import longevity.test.CustomGeneratorPool

object TestLongevityConfigs {

  val baseConfig = LongevityConfig()

  def configForKey(key: ConfigMatrixKey) = baseConfig.copy(
    backEnd = key.backEnd,
    autocreateSchema = key.autocreateSchema,
    optimisticLocking = key.optimisticLocking,
    writeTimestamps = key.writeTimestamps)

  val configMatrix = ConfigMatrixKey.values.map(key => key -> configForKey(key)).toMap

  val sparseConfigMatrix = ConfigMatrixKey.sparseValues.map(key => key -> configForKey(key)).toMap

  def contexts[M : ModelType](
    configKeys: Seq[ConfigMatrixKey],
    generators: CustomGeneratorPool = CustomGeneratorPool.empty)
  : Seq[LongevityContext[M]] =
    configKeys.map { key =>
      new LongevityContext(configForKey(key), generators)
    }

  def contextMatrix[M : ModelType](
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext[M]] =
    contexts(ConfigMatrixKey.values, generators)

  def sparseContextMatrix[M : ModelType](
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext[M]] =
    contexts(ConfigMatrixKey.sparseValues, generators)

  def cassandraOnlyContextMatrix[M : ModelType](
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext[M]] =
    contexts(Seq(cassandraConfigKey), generators)

  def mongoOnlyContextMatrix[M : ModelType](
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext[M]] =
    contexts(Seq(mongoConfigKey), generators)

  def sqliteOnlyContextMatrix[M : ModelType](
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext[M]] =
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

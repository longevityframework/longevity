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

  def contexts(
    configKeys: Seq[ConfigMatrixKey],
    modelType: ModelType,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty)
  : Seq[LongevityContext] =
    configKeys.map { key =>
      new LongevityContext(modelType, configForKey(key), generators)
    }

  def contextMatrix(
    modelType: ModelType,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(ConfigMatrixKey.values, modelType, generators)

  def sparseContextMatrix(
    modelType: ModelType,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(ConfigMatrixKey.sparseValues, modelType, generators)

  def cassandraOnlyContextMatrix(
    modelType: ModelType,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(Seq(cassandraConfigKey), modelType, generators)

  def mongoOnlyContextMatrix(
    modelType: ModelType,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(Seq(mongoConfigKey), modelType, generators)    

  def sqliteOnlyContextMatrix(
    modelType: ModelType,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(Seq(sqliteConfigKey), modelType, generators)

  val inMemConfigKey     = ConfigMatrixKey(InMem,     false, false, false)
  val cassandraConfigKey = ConfigMatrixKey(Cassandra, false, false, false)
  val mongoConfigKey     = ConfigMatrixKey(MongoDB,   false, false, false)
  val sqliteConfigKey    = ConfigMatrixKey(SQLite,    false, false, false)

  val inMemConfig     = configMatrix(inMemConfigKey)
  val cassandraConfig = configMatrix(cassandraConfigKey)
  val mongoConfig     = configMatrix(mongoConfigKey)
  val sqliteConfig    = configMatrix(sqliteConfigKey)

}

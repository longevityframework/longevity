package longevity

import longevity.config.Cassandra
import longevity.config.InMem
import longevity.config.LongevityConfig
import longevity.config.MongoDB
import longevity.config.SQLite
import longevity.context.LongevityContext
import longevity.model.DomainModel
import longevity.test.CustomGeneratorPool

object TestLongevityConfigs {

  val baseConfig = LongevityConfig()

  def configForKey(key: ConfigMatrixKey) = baseConfig.copy(
    backEnd = key.backEnd,
    autocreateSchema = key.autocreateSchema,
    optimisticLocking = key.optimisticLocking)

  val configMatrix = ConfigMatrixKey.values.map(key => key -> configForKey(key)).toMap

  val sparseConfigMatrix = ConfigMatrixKey.sparseValues.map(key => key -> configForKey(key)).toMap

  def contexts(
    configKeys: Seq[ConfigMatrixKey],
    domainModel: DomainModel,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty)
  : Seq[LongevityContext] =
    configKeys.map { key =>
      new LongevityContext(domainModel, configForKey(key), generators)
    }

  def contextMatrix(
    domainModel: DomainModel,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(ConfigMatrixKey.values, domainModel, generators)

  def sparseContextMatrix(
    domainModel: DomainModel,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(ConfigMatrixKey.sparseValues, domainModel, generators)

  def cassandraOnlyContextMatrix(
    domainModel: DomainModel,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(Seq(cassandraConfigKey), domainModel, generators)

  def mongoOnlyContextMatrix(
    domainModel: DomainModel,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(Seq(mongoConfigKey), domainModel, generators)    

  def sqliteOnlyContextMatrix(
    domainModel: DomainModel,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(Seq(sqliteConfigKey), domainModel, generators)

  val inMemConfigKey = ConfigMatrixKey(InMem, false, false)
  val cassandraConfigKey = ConfigMatrixKey(Cassandra, false, false)
  val mongoConfigKey = ConfigMatrixKey(MongoDB, false, false)
  val sqliteConfigKey = ConfigMatrixKey(SQLite, false, true)

  val inMemConfig = configMatrix(inMemConfigKey)
  val cassandraConfig = configMatrix(cassandraConfigKey)
  val mongoConfig = configMatrix(mongoConfigKey)
  val sqliteConfig = configMatrix(sqliteConfigKey)

}

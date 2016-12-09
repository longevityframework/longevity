package longevity

import longevity.config.Cassandra
import longevity.config.InMem
import longevity.config.LongevityConfig
import longevity.context.LongevityContext
import longevity.config.Mongo
import longevity.subdomain.Subdomain
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
    subdomain: Subdomain,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty)
  : Seq[LongevityContext] =
    configKeys.map { key =>
      new LongevityContext(subdomain, configForKey(key), generators)
    }

  def contextMatrix(
    subdomain: Subdomain,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(ConfigMatrixKey.values, subdomain, generators)

  def sparseContextMatrix(
    subdomain: Subdomain,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(ConfigMatrixKey.sparseValues, subdomain, generators)

  def mongoOnlyContextMatrix(
    subdomain: Subdomain,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(Seq(mongoConfigKey), subdomain, generators)    

  def cassandraOnlyContextMatrix(
    subdomain: Subdomain,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    contexts(Seq(cassandraConfigKey), subdomain, generators)    

  val inMemConfigKey = ConfigMatrixKey(InMem, false, false)
  val mongoConfigKey = ConfigMatrixKey(Mongo, false, false)
  val cassandraConfigKey = ConfigMatrixKey(Cassandra, false, false)

  val inMemConfig = configMatrix(inMemConfigKey)
  val mongoConfig = configMatrix(mongoConfigKey)
  val cassandraConfig = configMatrix(cassandraConfigKey)

}

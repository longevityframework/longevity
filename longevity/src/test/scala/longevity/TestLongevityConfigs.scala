package longevity

import emblem.emblematic.traversors.sync.CustomGeneratorPool
import longevity.context.Cassandra
import longevity.context.InMem
import longevity.context.LongevityConfig
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.Subdomain

object TestLongevityConfigs {

  val baseConfig = LongevityConfig()

  def configForKey(key: ConfigMatrixKey) = baseConfig.copy(
    backEnd = key.backEnd,
    autocreateSchema = key.autocreateSchema,
    optimisticLocking = key.optimisticLocking)

  val configMatrix = ConfigMatrixKey.values.map(key => key -> configForKey(key)).toMap

  val sparseConfigMatrix = ConfigMatrixKey.sparseValues.map(key => key -> configForKey(key)).toMap

  def contextMatrix(
    subdomain: Subdomain,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    configMatrix.values.toSeq.map { config =>
      new LongevityContext(subdomain, config, generators)
    }

  def sparseContextMatrix(
    subdomain: Subdomain,
    generators: CustomGeneratorPool = CustomGeneratorPool.empty): Seq[LongevityContext] =
    sparseConfigMatrix.values.toSeq.map { config =>
      new LongevityContext(subdomain, config, generators)
    }

  val inMemConfig = configMatrix(ConfigMatrixKey(InMem, false, false))
  val mongoConfig = configMatrix(ConfigMatrixKey(Mongo, false, false))
  val cassandraConfig = configMatrix(ConfigMatrixKey(Cassandra, false, false))

}

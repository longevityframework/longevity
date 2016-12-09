package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.config.Cassandra
import longevity.context.LongevityContext
import longevity.integration.model.derived

/** tests for cassandra repos that share tables in the presence of [[PolyCType]] */
class CassandraPolyReposSpec extends PolyReposSpec(
  new LongevityContext(
    derived.domainModel,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Cassandra, false, false))))

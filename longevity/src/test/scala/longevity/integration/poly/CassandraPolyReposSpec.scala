package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.integration.subdomain.derived

/** tests for cassandra repos that share tables in the presence of [[PolyCType]] */
class CassandraPolyReposSpec extends PolyReposSpec(
  new LongevityContext(
    derived.subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Cassandra, false, false))))

package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.integration.subdomain.derivedEntities

/** tests for cassandra repos that share tables in the presence of [[PolyEType]] */
class CassandraPolyReposSpec extends PolyReposSpec(
  new LongevityContext(
    derivedEntities.subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Cassandra, false, false))))

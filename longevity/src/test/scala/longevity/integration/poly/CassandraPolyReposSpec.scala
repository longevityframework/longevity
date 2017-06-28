package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.config.Cassandra
import longevity.context.LongevityContext
import longevity.integration.model.derived
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** tests for cassandra repos that share tables in the presence of [[PolyCType]] */
class CassandraPolyReposSpec extends PolyReposSpec(
  new LongevityContext[Future, derived.DomainModel](
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Cassandra, false, false, false))))

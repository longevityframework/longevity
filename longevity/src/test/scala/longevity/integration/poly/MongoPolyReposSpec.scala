package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.config.MongoDB
import longevity.integration.model.derived
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** tests for mongo repos that share tables in the presence of [[PolyCType]] */
class MongoPolyReposSpec extends PolyReposSpec(
  new LongevityContext[Future, derived.DomainModel](
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(MongoDB, false, false, false))))

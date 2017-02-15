package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.config.MongoDB
import longevity.integration.model.derived

/** tests for mongo repos that share tables in the presence of [[PolyCType]] */
class MongoPolyReposSpec extends PolyReposSpec(
  new LongevityContext(
    derived.domainModel,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(MongoDB, false, false, false))))

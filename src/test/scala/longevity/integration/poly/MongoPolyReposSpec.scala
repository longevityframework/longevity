package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.integration.subdomain.derived

/** tests for mongo repos that share tables in the presence of [[PolyEType]] */
class MongoPolyReposSpec extends PolyReposSpec(
  new LongevityContext(
    derived.subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Mongo, false, false))))

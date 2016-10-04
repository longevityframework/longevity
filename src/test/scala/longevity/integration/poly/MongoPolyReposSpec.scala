package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.integration.subdomain.derivedEntities

/** tests for mongo repos that share tables in the presence of [[PolyEType]] */
class MongoPolyReposSpec extends PolyReposSpec(
  new LongevityContext(
    derivedEntities.subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Mongo, false, false))))

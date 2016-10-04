package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.context.InMem
import longevity.integration.subdomain.derivedEntities

/** tests for in-memory repos that share tables in the presence of [[PolyEType]] */
class InMemPolyReposSpec extends PolyReposSpec(
  new LongevityContext(
    derivedEntities.subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(InMem, false, false))))

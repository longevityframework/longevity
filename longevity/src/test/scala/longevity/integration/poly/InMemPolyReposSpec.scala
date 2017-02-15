package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.config.InMem
import longevity.integration.model.derived

/** tests for in-memory repos that share tables in the presence of [[PolyCType]] */
class InMemPolyReposSpec extends PolyReposSpec(
  new LongevityContext(
    derived.domainModel,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(InMem, false, false, false))))

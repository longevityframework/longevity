package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.config.InMem
import longevity.integration.model.derived
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** tests for in-memory repos that share tables in the presence of [[PolyCType]] */
class InMemPolyReposSpec extends PolyReposSpec(
  new LongevityContext[Future, derived.DomainModel](
    TestLongevityConfigs.configForKey(ConfigMatrixKey(InMem, false, false, false))))

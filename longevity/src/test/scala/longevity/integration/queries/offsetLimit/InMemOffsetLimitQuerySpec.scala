package longevity.integration.queries.offsetLimit

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.integration.queries.queryTestsExecutionContext

class InMemOffsetLimitQuerySpec extends OffsetLimitQuerySpec(
  new LongevityContext(TestLongevityConfigs.inMemConfig))

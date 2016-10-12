package longevity.integration.queries.offsetLimit

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext

class InMemOffsetLimitQuerySpec extends OffsetLimitQuerySpec(
  new LongevityContext(OffsetLimitQuerySpec.subdomain, TestLongevityConfigs.inMemConfig))

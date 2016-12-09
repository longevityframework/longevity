package longevity.integration.queries.offsetLimit

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext

class MongoOffsetLimitQuerySpec extends OffsetLimitQuerySpec(
  new LongevityContext(OffsetLimitQuerySpec.domainModel, TestLongevityConfigs.mongoConfig))

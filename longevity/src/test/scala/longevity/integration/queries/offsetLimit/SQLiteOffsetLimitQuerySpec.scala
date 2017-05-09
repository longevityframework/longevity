package longevity.integration.queries.offsetLimit

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext

class SQLiteOffsetLimitQuerySpec extends OffsetLimitQuerySpec(
  new LongevityContext(OffsetLimitQuerySpec.modelType, TestLongevityConfigs.sqliteConfig),
  false)

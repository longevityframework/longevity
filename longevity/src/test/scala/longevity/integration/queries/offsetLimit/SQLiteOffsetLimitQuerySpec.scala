package longevity.integration.queries.offsetLimit

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.integration.queries.queryTestsExecutionContext

class SQLiteOffsetLimitQuerySpec extends OffsetLimitQuerySpec(
  new LongevityContext(TestLongevityConfigs.sqliteConfig),
  false)

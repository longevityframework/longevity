package longevity.integration.queries.offsetLimit

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext

class SQLiteOffsetLimitQuerySpec extends OffsetLimitQuerySpec(
  new LongevityContext[DomainModel](TestLongevityConfigs.sqliteConfig),
  false)

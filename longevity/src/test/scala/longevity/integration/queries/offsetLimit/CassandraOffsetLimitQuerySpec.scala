package longevity.integration.queries.offsetLimit

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.integration.queries.queryTestsExecutionContext

class CassandraOffsetLimitQuerySpec extends OffsetLimitQuerySpec(
  new LongevityContext(TestLongevityConfigs.cassandraConfig),
  false)

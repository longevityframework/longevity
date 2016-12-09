package longevity.integration.queries.offsetLimit

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext

class CassandraOffsetLimitQuerySpec extends OffsetLimitQuerySpec(
  new LongevityContext(OffsetLimitQuerySpec.domainModel, TestLongevityConfigs.cassandraConfig),
  false)

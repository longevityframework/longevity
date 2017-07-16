package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.config.Cassandra
import longevity.context.LongevityContext
import scala.concurrent.ExecutionContext.Implicits.global

/** optimistic locking tests for cassandra back end */
class CassandraOptLockSpec extends OptLockSpec(
  new LongevityContext(
    TestLongevityConfigs.configForKey(ConfigMatrixKey(Cassandra, true, true, false))))

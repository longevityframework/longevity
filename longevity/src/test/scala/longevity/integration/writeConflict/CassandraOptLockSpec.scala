package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.config.Cassandra
import longevity.context.LongevityContext
import longevity.integration.model.basics

/** optimistic locking tests for cassandra back end */
class CassandraOptLockSpec extends OptLockSpec(
  new LongevityContext(
    basics.domainModel,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Cassandra, true, true, false))))

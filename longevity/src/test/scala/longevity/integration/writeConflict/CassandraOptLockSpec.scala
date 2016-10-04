package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.integration.subdomain.basics

/** optimistic locking tests for cassandra back end */
class CassandraOptLockSpec extends OptLockSpec(
  new LongevityContext(
    basics.subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Cassandra, true, true))))

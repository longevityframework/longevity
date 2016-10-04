package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.context.InMem
import longevity.integration.subdomain.basics

/** optimistic locking tests for inmem back end */
class InMemOptLockSpec extends OptLockSpec(
  new LongevityContext(
    basics.subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(InMem, true, true))))


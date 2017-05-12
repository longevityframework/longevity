package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.config.InMem
import longevity.integration.model.basics

/** optimistic locking tests for inmem back end */
class InMemOptLockSpec extends OptLockSpec(
  new LongevityContext[basics.DomainModel](
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(InMem, true, true, false))))

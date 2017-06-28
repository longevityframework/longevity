package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.config.InMem
import scala.concurrent.ExecutionContext.Implicits.global

/** optimistic locking tests for inmem back end */
class InMemOptLockSpec extends OptLockSpec(
  new LongevityContext(
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(InMem, true, true, false))))

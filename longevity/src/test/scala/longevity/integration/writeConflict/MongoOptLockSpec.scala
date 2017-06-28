package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.config.MongoDB
import scala.concurrent.ExecutionContext.Implicits.global

/** optimistic locking tests for mongo back end */
class MongoOptLockSpec extends OptLockSpec(
  new LongevityContext(
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(MongoDB, true, true, false))))

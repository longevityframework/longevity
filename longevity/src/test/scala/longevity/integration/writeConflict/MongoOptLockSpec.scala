package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.config.MongoDB
import longevity.integration.model.basics

/** optimistic locking tests for mongo back end */
class MongoOptLockSpec extends OptLockSpec(
  new LongevityContext[basics.DomainModel](
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(MongoDB, true, true, false))))

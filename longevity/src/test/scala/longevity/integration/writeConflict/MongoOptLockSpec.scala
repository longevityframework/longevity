package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.config.Mongo
import longevity.integration.model.basics

/** optimistic locking tests for mongo back end */
class MongoOptLockSpec extends OptLockSpec(
  new LongevityContext(
    basics.domainModel,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Mongo, true, true))))

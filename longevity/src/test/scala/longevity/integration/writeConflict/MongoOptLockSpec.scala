package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.integration.subdomain.basics

/** optimistic locking tests for mongo back end */
class MongoOptLockSpec extends OptLockSpec(
  new LongevityContext(
    basics.subdomain,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(Mongo, true, true))))

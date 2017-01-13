package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.config.SQLite
import longevity.context.LongevityContext
import longevity.integration.model.basics

/** optimistic locking tests for SQLite back end */
class SQLiteOptLockSpec extends OptLockSpec(
  new LongevityContext(
    basics.domainModel,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(SQLite, true, true))))

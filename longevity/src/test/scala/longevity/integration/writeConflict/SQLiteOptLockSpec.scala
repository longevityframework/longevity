package longevity.integration.writeConflict

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.config.SQLite
import longevity.context.LongevityContext
import scala.concurrent.ExecutionContext.Implicits.global

/** optimistic locking tests for SQLite back end */
class SQLiteOptLockSpec extends OptLockSpec(
  new LongevityContext(
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(SQLite, true, true, false))))

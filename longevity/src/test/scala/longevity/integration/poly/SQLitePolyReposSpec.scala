package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.config.SQLite
import longevity.context.LongevityContext
import longevity.integration.model.derived

/** tests for SQLite repos that share tables in the presence of [[PolyCType]] */
class SQLitePolyReposSpec extends PolyReposSpec(
  new LongevityContext(
    derived.domainModel,
    TestLongevityConfigs.configMatrix(ConfigMatrixKey(SQLite, false, false))))

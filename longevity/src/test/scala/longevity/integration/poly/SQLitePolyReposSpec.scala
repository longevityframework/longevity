package longevity.integration.poly

import longevity.ConfigMatrixKey
import longevity.TestLongevityConfigs
import longevity.config.SQLite
import longevity.context.LongevityContext
import longevity.integration.model.derived
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** tests for SQLite repos that share tables in the presence of [[PolyCType]] */
class SQLitePolyReposSpec extends PolyReposSpec(
  new LongevityContext[Future, derived.DomainModel](
    TestLongevityConfigs.configForKey(ConfigMatrixKey(SQLite, false, false, false))))

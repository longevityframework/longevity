package longevity.migrations.integration.failures

import longevity.config.SQLite

class SQLiteFailuresMigrationSpec extends FailuresMigrationSpec {
  protected def backEnd = SQLite
}

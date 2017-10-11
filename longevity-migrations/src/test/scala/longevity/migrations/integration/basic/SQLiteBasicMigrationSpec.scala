package longevity.migrations.integration.basic

import longevity.config.SQLite

class SQLiteBasicMigrationSpec extends BasicMigrationSpec {
  protected def backEnd = SQLite
}

package longevity.migrations.integration.poly

import longevity.config.SQLite

class SQLitePolyMigrationSpec extends PolyMigrationSpec {
  protected def backEnd = SQLite
}

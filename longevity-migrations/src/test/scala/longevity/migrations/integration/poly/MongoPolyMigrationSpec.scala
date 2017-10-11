package longevity.migrations.integration.poly

import longevity.config.MongoDB

class MongoPolyMigrationSpec extends PolyMigrationSpec {
  protected def backEnd = MongoDB
}

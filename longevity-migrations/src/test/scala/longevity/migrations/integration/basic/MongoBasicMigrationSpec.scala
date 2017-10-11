package longevity.migrations.integration.basic

import longevity.config.MongoDB

class MongoBasicMigrationSpec extends BasicMigrationSpec {
  protected def backEnd = MongoDB
}

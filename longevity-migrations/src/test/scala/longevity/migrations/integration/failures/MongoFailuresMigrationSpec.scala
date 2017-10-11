package longevity.migrations.integration.failures

import longevity.config.MongoDB

class MongoFailuresMigrationSpec extends FailuresMigrationSpec {
  protected def backEnd = MongoDB
}

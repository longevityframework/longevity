package longevity.migrations.integration.failures

import longevity.config.Cassandra

class CassandraFailuresMigrationSpec extends FailuresMigrationSpec {
  protected def backEnd = Cassandra
}

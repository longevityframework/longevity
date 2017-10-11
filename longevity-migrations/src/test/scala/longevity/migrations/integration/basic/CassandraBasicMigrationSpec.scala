package longevity.migrations.integration.basic

import longevity.config.Cassandra

class CassandraBasicMigrationSpec extends BasicMigrationSpec {
  protected def backEnd = Cassandra
}

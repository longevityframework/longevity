package longevity.migrations.integration.poly

import longevity.config.Cassandra

class CassandraPolyMigrationSpec extends PolyMigrationSpec {
  protected def backEnd = Cassandra
}

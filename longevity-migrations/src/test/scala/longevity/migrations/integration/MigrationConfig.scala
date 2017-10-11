package longevity.migrations.integration

import longevity.config.{ BackEnd, LongevityConfig }

object MigrationConfig {

  private val baseConfig = LongevityConfig.fromTypesafeConfig()

  def config(backEnd: BackEnd, keyspace: String): LongevityConfig =
    baseConfig.copy(
      backEnd = backEnd,
      cassandra = baseConfig.cassandra.copy(keyspace = keyspace),
      mongodb = baseConfig.mongodb.copy(db = keyspace),
      jdbc = baseConfig.jdbc.copy(url = s"jdbc:sqlite:$keyspace.db"))

}

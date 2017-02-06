package longevity.config

/** the test configuration
 *
 * @param mongodb the test configuration for MongoDB
 * @param cassandra the test configuration for Cassandra
 * @param jdbc the JDBC configuration. used by the SQLite back end
 *
 * @see LongevityConfig
 */
case class TestConfig(
  cassandra: CassandraConfig,
  mongodb: MongoDBConfig,
  jdbc: JdbcConfig)

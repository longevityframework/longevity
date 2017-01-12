package longevity.config

/** the test configuration
 *
 * @param mongodb the test configuration for MongoDB
 * @param cassandra the test configuration for Cassandra
 *
 * @see LongevityConfig
 */
case class TestConfig(
  cassandra: CassandraConfig,
  mongodb: MongoDBConfig,
  sqlite: SQLiteConfig)

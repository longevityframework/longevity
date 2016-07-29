package longevity.context

/** the test configuration
 *
 * @see LongevityConfig
 */
case class TestConfig(
  mongodb: MongoConfig,
  cassandra: CassandraConfig)

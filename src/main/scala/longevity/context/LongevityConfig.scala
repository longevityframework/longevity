package longevity.context

/** the longevity configuration. see the `reference.conf` resource file for all
 * the longevity config settings, and their defaults.
 */
case class LongevityConfig(
  optimisticLocking: Boolean,
  mongodb: MongoConfig,
  cassandra: CassandraConfig,
  test: TestConfig)

package longevity.context

/** the longevity configuration. see the `reference.conf` resource file for all
 * the longevity config settings, and their defaults.
 *
 * @param autogenerateSchema should longevity autogenerate schema when the repositories are created?
 * @param optimisticLocking is optimistic locking turned on?
 * @param mongodb the mongo configuration
 * @param cassandra the cassandra configuration
 * @param test the test configuration
 */
case class LongevityConfig(
  autogenerateSchema: Boolean,
  optimisticLocking: Boolean,
  mongodb: MongoConfig,
  cassandra: CassandraConfig,
  test: TestConfig)
extends PersistenceConfig

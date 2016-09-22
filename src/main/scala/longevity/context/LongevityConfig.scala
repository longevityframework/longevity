package longevity.context

/** the longevity configuration. see the `reference.conf` resource file for all
 * the longevity config settings, and their defaults.
 *
 * @param backEnd the back end used by the longevity context
 * @param autocreateSchema should longevity autocreate schema when the repositories are created?
 * @param optimisticLocking is optimistic locking turned on?
 * @param mongodb the mongo configuration
 * @param cassandra the cassandra configuration
 * @param test the test configuration
 */
case class LongevityConfig(
  backEnd: BackEnd,
  autocreateSchema: Boolean,
  optimisticLocking: Boolean,
  mongodb: MongoConfig,
  cassandra: CassandraConfig,
  test: TestConfig)
extends PersistenceConfig

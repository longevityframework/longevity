package longevity.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import longevity.exceptions.context.LongevityConfigException

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

/** contains a factory method for [[LongevityConfig]] */
object LongevityConfig {

  /** builds a [[LongevityConfig]] from a Typesafe Config
   *
   * @param typesafeConfig the typesafe configuration. defaults to typesafe
   * config's `ConfigFactory.load()`
   * 
   * @throws longevity.exceptions.context.LongevityConfigException if the
   * typesafe configuration does not adequately specify the LongevityConfig
   */
  def apply(typesafeConfig: Config = ConfigFactory.load()): LongevityConfig = {
    import configs.syntax._
    typesafeConfig.get[LongevityConfig]("longevity").valueOrThrow {
      error => new LongevityConfigException(error.configException)
    }
  }

}

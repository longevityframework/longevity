package longevity.context

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import emblem.emblematic.traversors.sync.CustomGeneratorPool
import emblem.emblematic.traversors.sync.TestDataGenerator
import longevity.exceptions.context.LongevityConfigException
import longevity.json.JsonMarshaller
import longevity.json.JsonUnmarshaller
import longevity.persistence.RepoPoolBuilder.buildRepoPool
import longevity.subdomain.Subdomain

/** contains a factory method for [[LongevityContext]] objects */
object LongevityContext {

  /** constructs and returns a [[LongevityContext]]
   * 
   * @param subdomain the subdomain
   * @param persistenceStrategy the back end for this longevity
   * context. defaults to [[Mongo]]
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data. defaults to empty
   * @param typesafeConfig the typesafe configuration
   * 
   * @throws longevity.exceptions.context.LongevityConfigException if the
   * typesafe configuration does not adequately specify the LongevityConfig
   */
  def apply(
    subdomain: Subdomain,
    persistenceStrategy: BackEnd = Mongo,
    customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty,
    typesafeConfig: Config = ConfigFactory.load())
  : LongevityContext = {
    val config = {
      import configs.syntax._
      typesafeConfig.get[LongevityConfig]("longevity").valueOrThrow {
        error => new LongevityConfigException(error.configException)
      }
    }
    new LongevityContext(
      subdomain,
      persistenceStrategy,
      customGeneratorPool,
      config)
  }

}

/** the longevity managed portion of the
 * [[http://martinfowler.com/bliki/BoundedContext.html bounded context]] for
 * your [[http://bit.ly/1BPZfIW subdomain]]. the bounded context is a capture
 * of the strategies and tools used by the applications relating to your
 * subdomain. in other words, those tools that speak the language of the
 * subdomain.
 * 
 * @param subdomain the subdomain
 * @param persistenceStrategy the back end for this longevity
 * context. defaults to [[Mongo]]
 * @param customGeneratorPool a collection of custom generators to use when
 * generating test data. defaults to empty
 * @param config the longevity configuration
 */
final class LongevityContext(
  val subdomain: Subdomain,
  val persistenceStrategy: BackEnd = Mongo,
  val customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty,
  val config: LongevityConfig)
extends PersistenceContext with TestContext with JsonContext {

  lazy val repoPool = buildRepoPool(subdomain, persistenceStrategy, config, false)

  lazy val testRepoPool = buildRepoPool(subdomain, persistenceStrategy, config, true)
  lazy val inMemTestRepoPool = buildRepoPool(subdomain, InMem, config, true)
  lazy val testDataGenerator = new TestDataGenerator(subdomain.emblematic, customGeneratorPool)

  lazy val jsonMarshaller = new JsonMarshaller(subdomain)
  lazy val jsonUnmarshaller = new JsonUnmarshaller(subdomain)

}

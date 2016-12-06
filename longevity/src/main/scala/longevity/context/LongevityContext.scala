package longevity.context

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import emblem.emblematic.traversors.sync.CustomGeneratorPool
import emblem.emblematic.traversors.sync.TestDataGenerator
import longevity.json.JsonMarshaller
import longevity.json.JsonUnmarshaller
import longevity.persistence.RepoPoolBuilder.buildRepoPool
import longevity.subdomain.Subdomain

/** contains a factory method for [[LongevityContext]] objects */
object LongevityContext {

  /** constructs and returns a [[LongevityContext]] using a Typesafe config
   * 
   * @param subdomain the subdomain
   *
   * @param typesafeConfig the typesafe configuration. defaults to typesafe
   * config's `ConfigFactory.load()`
   *
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data. defaults to empty
   * 
   * @throws longevity.exceptions.context.LongevityConfigException if the
   * typesafe configuration does not adequately specify the LongevityConfig
   */
  def apply(
    subdomain: Subdomain,
    typesafeConfig: Config = ConfigFactory.load(),
    customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)
  : LongevityContext =
    new LongevityContext(subdomain, LongevityConfig(typesafeConfig), customGeneratorPool)

  /** constructs and returns a [[LongevityContext]] using a [[LongevityConfig]]
   * 
   * @param subdomain the subdomain
   *
   * @param config the longevity configuration
   *
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data. defaults to empty
   */
  def apply(
    subdomain: Subdomain,
    config: LongevityConfig,
    customGeneratorPool: CustomGeneratorPool)
  : LongevityContext =
    new LongevityContext(subdomain, config, customGeneratorPool)

  /** constructs and returns a [[LongevityContext]] using a [[LongevityConfig]].
   * the context will have an empty set of custom generators
   * 
   * @param subdomain the subdomain
   *
   * @param config the longevity configuration
   */
  def apply(subdomain: Subdomain, config: LongevityConfig): LongevityContext = 
    new LongevityContext(subdomain, config, CustomGeneratorPool.empty)

}

/** the longevity managed portion of the
 * [[http://martinfowler.com/bliki/BoundedContext.html bounded context]] for
 * your [[http://bit.ly/1BPZfIW subdomain]]. the bounded context is a capture
 * of the strategies and tools used by the applications relating to your
 * subdomain. in other words, those tools that speak the language of the
 * subdomain.
 * 
 * @param subdomain the subdomain
 *
 * @param config the longevity configuration
 *
 * @param customGeneratorPool a collection of custom generators to use when
 * generating test data. defaults to empty
 */
final class LongevityContext(
  val subdomain: Subdomain,
  val config: LongevityConfig,
  val customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)
extends PersistenceContext with TestContext with JsonContext {

  /** constructs a [[LongevityContext]] using a Typesafe config
   * 
   * @param subdomain the subdomain
   * 
   * @param typesafeConfig the typesafe configuration
   * 
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data
   * 
   * @throws longevity.exceptions.context.LongevityConfigException if the
   * typesafe configuration does not adequately specify the LongevityConfig
   */
  def this(subdomain: Subdomain, typesafeConfig: Config, customGeneratorPool: CustomGeneratorPool) =
    this(subdomain, LongevityConfig(typesafeConfig), customGeneratorPool)

  /** constructs a [[LongevityContext]] with an empty set of custom generators
   * using a Typesafe config
   * 
   * @param subdomain the subdomain
   * 
   * @param typesafeConfig the typesafe configuration
   */
  def this(subdomain: Subdomain, typesafeConfig: Config) =
    this(subdomain, typesafeConfig, CustomGeneratorPool.empty)

  lazy val repoPool = buildRepoPool(subdomain, config.backEnd, config, false)

  lazy val testRepoPool = buildRepoPool(subdomain, config.backEnd, config, true)
  lazy val inMemTestRepoPool = buildRepoPool(subdomain, InMem, config, true)
  lazy val testDataGenerator = new TestDataGenerator(subdomain.emblematic, customGeneratorPool)

  lazy val jsonMarshaller = new JsonMarshaller(subdomain)
  lazy val jsonUnmarshaller = new JsonUnmarshaller(subdomain)

  override def toString = s"""|LongevityContext(
                              |  $subdomain,
                              |  $config)""".stripMargin

}

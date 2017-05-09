package longevity.context

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import longevity.config.InMem
import longevity.config.LongevityConfig
import longevity.json.JsonMarshaller
import longevity.json.JsonUnmarshaller
import longevity.persistence.RepoBuilder.buildRepo
import longevity.model.ModelType
import longevity.test.CustomGeneratorPool
import longevity.test.TestDataGenerator

/** contains a factory method for [[LongevityContext]] objects */
object LongevityContext {

  /** creates and returns a [[LongevityContext]] using a Typesafe config
   * 
   * @param domainModel the domain model
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
    domainModel: ModelType,
    typesafeConfig: Config = ConfigFactory.load(),
    customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)
  : LongevityContext =
    new LongevityContext(domainModel, LongevityConfig(typesafeConfig), customGeneratorPool)

  /** creates and returns a [[LongevityContext]] using a
   * [[longevity.config.LongevityConfig LongevityConfig]]
   * 
   * @param domainModel the domain model
   *
   * @param config the longevity configuration
   *
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data. defaults to empty
   */
  def apply(
    domainModel: ModelType,
    config: LongevityConfig,
    customGeneratorPool: CustomGeneratorPool)
  : LongevityContext =
    new LongevityContext(domainModel, config, customGeneratorPool)

  /** creates and returns a [[LongevityContext]] using a
   * [[longevity.config.LongevityConfig LongevityConfig]].
   * the context will have an empty set of custom generators
   * 
   * @param domainModel the domain model
   *
   * @param config the longevity configuration
   */
  def apply(domainModel: ModelType, config: LongevityConfig): LongevityContext = 
    new LongevityContext(domainModel, config, CustomGeneratorPool.empty)

}

/** a collection of longevity utilities applicable to a specific
 * [[longevity.model.ModelType ModelType]].
 *
 * @constructor creates a [[LongevityContext]] using a
 * [[longevity.config.LongevityConfig LongevityConfig]]
 * 
 * @param domainModel the domain model
 *
 * @param config the longevity configuration
 *
 * @param customGeneratorPool a collection of custom generators to use when
 * generating test data. defaults to empty
 */
final class LongevityContext(
  val domainModel: ModelType,
  val config: LongevityConfig,
  val customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)
extends PersistenceContext with TestContext with JsonContext {

  /** constructs a [[LongevityContext]] using a Typesafe config
   * 
   * @param domainModel the domain model
   * 
   * @param typesafeConfig the typesafe configuration
   * 
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data
   * 
   * @throws longevity.exceptions.context.LongevityConfigException if the
   * typesafe configuration does not adequately specify the LongevityConfig
   */
  def this(domainModel: ModelType, typesafeConfig: Config, customGeneratorPool: CustomGeneratorPool) =
    this(domainModel, LongevityConfig(typesafeConfig), customGeneratorPool)

  /** constructs a [[LongevityContext]] with an empty set of custom generators
   * using a Typesafe config
   * 
   * @param domainModel the domain model
   * 
   * @param typesafeConfig the typesafe configuration
   */
  def this(domainModel: ModelType, typesafeConfig: Config) =
    this(domainModel, typesafeConfig, CustomGeneratorPool.empty)

  lazy val repo = buildRepo(domainModel, config.backEnd, config, false)

  lazy val testRepo = buildRepo(domainModel, config.backEnd, config, true)
  lazy val inMemTestRepo = buildRepo(domainModel, InMem, config, true)
  lazy val testDataGenerator = TestDataGenerator(domainModel.emblematic, customGeneratorPool)

  lazy val jsonMarshaller = new JsonMarshaller(domainModel)
  lazy val jsonUnmarshaller = new JsonUnmarshaller(domainModel)

  override def toString = s"""|LongevityContext(
                              |  $domainModel,
                              |  $config)""".stripMargin

}

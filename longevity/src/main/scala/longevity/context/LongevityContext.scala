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
   * @tparam M the model
   *
   * @param typesafeConfig the typesafe configuration. defaults to typesafe
   * config's `ConfigFactory.load()`
   *
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data. defaults to empty
   * 
   * @param modelType the model type
   *
   * @throws longevity.exceptions.context.LongevityConfigException if the
   * typesafe configuration does not adequately specify the LongevityConfig
   */
  def apply[M : ModelType](
    typesafeConfig: Config = ConfigFactory.load(),
    customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)
  : LongevityContext[M] =
    new LongevityContext(LongevityConfig(typesafeConfig), customGeneratorPool)

  /** creates and returns a [[LongevityContext]] using a
   * [[longevity.config.LongevityConfig LongevityConfig]]
   *
   * @param modelType the model type
   *
   * @param config the longevity configuration
   *
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data. defaults to empty
   * 
   * @tparam M the model
   */
  def apply[M : ModelType](config: LongevityConfig, customGeneratorPool: CustomGeneratorPool)
  : LongevityContext[M] =
    new LongevityContext(config, customGeneratorPool)

  /** creates and returns a [[LongevityContext]] using a [[longevity.config.LongevityConfig
   * LongevityConfig]]. the context will have an empty set of custom generators
   * 
   * @tparam M the model
   *
   * @param config the longevity configuration
   *
   * @param modelType the model type
   */
  def apply[M : ModelType](config: LongevityConfig): LongevityContext[M] =
    new LongevityContext(config, CustomGeneratorPool.empty)

}

/** a collection of longevity utilities applicable to a specific [[longevity.model.ModelType
 * ModelType]].
 *
 * @tparam M the model
 *
 * @constructor creates a [[LongevityContext]] using a [[longevity.config.LongevityConfig
 * LongevityConfig]]
 *
 * @param config the longevity configuration
 *
 * @param customGeneratorPool a collection of custom generators to use when generating test data.
 * defaults to empty
 * 
 * @param modelType the model type
 */
final class LongevityContext[M](
  val config: LongevityConfig,
  val customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)(
  implicit val modelType: ModelType[M])
extends PersistenceContext[M] with TestContext[M] with JsonContext {

  /** constructs a [[LongevityContext]] using a Typesafe config
   * 
   * @tparam M the model
   *
   * @param typesafeConfig the typesafe configuration
   * 
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data
   * 
   * @param modelType the model type
   * 
   * @throws longevity.exceptions.context.LongevityConfigException if the
   * typesafe configuration does not adequately specify the LongevityConfig
   */
  def this(typesafeConfig: Config, customGeneratorPool: CustomGeneratorPool)(implicit modelType: ModelType[M]) =
    this(LongevityConfig(typesafeConfig), customGeneratorPool)

  /** constructs a [[LongevityContext]] with an empty set of custom generators using a Typesafe config
   * 
   * @tparam M the model
   * 
   * @param typesafeConfig the typesafe configuration
   *
   * @param modelType the model type
   */
  def this(typesafeConfig: Config)(implicit modelType: ModelType[M]) =
    this(typesafeConfig, CustomGeneratorPool.empty)

  lazy val repo = buildRepo(modelType, config.backEnd, config, false)

  lazy val testRepo = buildRepo(modelType, config.backEnd, config, true)
  lazy val inMemTestRepo = buildRepo(modelType, InMem, config, true)
  lazy val testDataGenerator = TestDataGenerator(modelType.emblematic, customGeneratorPool)

  lazy val jsonMarshaller = new JsonMarshaller(modelType)
  lazy val jsonUnmarshaller = new JsonUnmarshaller(modelType)

  override def toString = s"""|LongevityContext(
                              |  $modelType,
                              |  $config)""".stripMargin

}

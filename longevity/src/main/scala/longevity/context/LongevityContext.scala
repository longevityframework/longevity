package longevity.context

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import longevity.config.InMem
import longevity.config.LongevityConfig
import longevity.effect.Effect
import longevity.json.JsonMarshaller
import longevity.json.JsonUnmarshaller
import longevity.persistence.Repo
import longevity.model.ModelType
import longevity.test.CustomGeneratorPool
import longevity.test.TestDataGenerator

/** contains a factory method for [[LongevityContext]] objects */
object LongevityContext {

  /** creates and returns a [[LongevityContext]] using a Typesafe config
   *
   * @tparam F the effect
   * @tparam M the model
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
  def apply[F[_] : Effect, M : ModelType](
    typesafeConfig: Config = ConfigFactory.load(),
    customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)
  : LongevityContext[F, M] =
    new LongevityContext(LongevityConfig.fromTypesafeConfig(typesafeConfig), customGeneratorPool)

  /** creates and returns a [[LongevityContext]] using a
   * [[longevity.config.LongevityConfig LongevityConfig]]
   *
   * @tparam F the effect
   * @tparam M the model
   *
   * @param config the longevity configuration
   *
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data. defaults to empty
   */
  def apply[F[_] : Effect, M : ModelType](config: LongevityConfig, customGeneratorPool: CustomGeneratorPool)
  : LongevityContext[F, M] =
    new LongevityContext(config, customGeneratorPool)

  /** creates and returns a [[LongevityContext]] using a [[longevity.config.LongevityConfig
   * LongevityConfig]]. the context will have an empty set of custom generators
   *
   * @tparam F the effect
   * @tparam M the model
   *
   * @param config the longevity configuration
   */
  def apply[F[_] : Effect, M : ModelType](config: LongevityConfig): LongevityContext[F, M] =
    new LongevityContext(config, CustomGeneratorPool.empty)

}

/** a collection of longevity utilities applicable to a specific [[longevity.model.ModelType
 * ModelType]].
 *
 * @tparam F the effect
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
 * @param effect the effect type class
 *
 * @param modelType the model type
 */
final class LongevityContext[F[_], M](
  val config: LongevityConfig,
  val customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)(
  implicit val effect: Effect[F], val modelType: ModelType[M])
extends PersistenceContext[F, M] with TestContext[F, M] with JsonContext {

  /** constructs a [[LongevityContext]] using a Typesafe config
   * 
   * @tparam F the effect
   * @tparam M the model
   *
   * @param typesafeConfig the typesafe configuration
   * 
   * @param customGeneratorPool a collection of custom generators to use when
   * generating test data
   *
   * @param effect the effect type class
   *
   * @param modelType the model type
   * 
   * @throws longevity.exceptions.context.LongevityConfigException if the
   * typesafe configuration does not adequately specify the LongevityConfig
   */
  def this(typesafeConfig: Config, customGeneratorPool: CustomGeneratorPool)(
    implicit effect: Effect[F], modelType: ModelType[M]) =
    this(LongevityConfig.fromTypesafeConfig(typesafeConfig), customGeneratorPool)

  /** constructs a [[LongevityContext]] with an empty set of custom generators using a Typesafe config
   * 
   * @tparam F the effect
   * @tparam M the model
   * 
   * @param typesafeConfig the typesafe configuration
   *
   * @param effect the effect type class
   *
   * @param modelType the model type
   */
  def this(typesafeConfig: Config)(implicit effect: Effect[F], modelType: ModelType[M]) =
    this(typesafeConfig, CustomGeneratorPool.empty)

  lazy val repo          = Repo(effect, modelType, config.backEnd, config, false)
  lazy val testRepo      = Repo(effect, modelType, config.backEnd, config, true)
  lazy val inMemTestRepo = Repo(effect, modelType, InMem,          config, true)

  def testDataGenerator = () => testDataGenerator(System.currentTimeMillis)
  def testDataGenerator(seed: Long) = TestDataGenerator(modelType.emblematic, customGeneratorPool, seed)

  lazy val jsonMarshaller = new JsonMarshaller(modelType)
  lazy val jsonUnmarshaller = new JsonUnmarshaller(modelType)

  override def toString = s"""|LongevityContext(
                              |  $modelType,
                              |  $config)""".stripMargin

}

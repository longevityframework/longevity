package longevity.migrations

import com.typesafe.config.{ Config, ConfigFactory }
import longevity.config.LongevityConfig
import longevity.model.{ ModelType, PEv, PType }

/** describes a migration from one version of a domain model to another
 *
 * @tparam M1 the initial version of the model - the one we are migrating from
 * @tparam M2 the final version of the model - the one we are migrating to
 * @param config1 the configuration for the initial version of the model
 * @param config2 the configuration for the final version of the model
 * @param steps the steps to perform to complete the migration
 */
class Migration[M1 : ModelType, M2: ModelType](
  val config1: LongevityConfig,
  val config2: LongevityConfig,
  steps: Seq[MigrationStep[M1, M2]]) {

  private val modelType1 = implicitly[ModelType[M1]]
  private val modelType2 = implicitly[ModelType[M2]]

  /** checks if the migration is valid. returns a summary of the validity check */
  def validate: ValidationResult = {
    val (pTypes1, pTypes2) = steps.foldLeft(
      (Set[PType[M1, _]](), Set[PType[M2, _]]())) {
      case ((pTypes1, pTypes2), step) =>
        step match {
          case s: DropStep[M1, M2, _] =>
            val pType1 = modelType1.pTypePool(s.pEv1.key)
            (pTypes1 + pType1, pTypes2)
          case s: CreateStep[M1, M2, _] =>
            val pType2 = modelType2.pTypePool(s.pEv2.key)
            (pTypes1, pTypes2 + pType2)
          case s: UpdateStep[M1, M2, _, _] =>
            val pType1 = modelType1.pTypePool(s.pEv1.key)
            val pType2 = modelType2.pTypePool(s.pEv2.key)
            (pTypes1 + pType1, pTypes2 + pType2)
        }
    }
    val missingPTypes1 = modelType1.pTypePool.values.toSet -- pTypes1
    val missingPTypes2 = modelType2.pTypePool.values.toSet -- pTypes2
    val errors1 = missingPTypes1.map { pType => new InitialPersistentMissing(pType.name) }
    val errors2 = missingPTypes2.map { pType => new FinalPersistentMissing(pType.name) }
    new ValidationResult((errors1 ++ errors2).toSeq)
  }

}

object Migration {

  /** creates a migration builder
   *
   * @tparam M1 the initial version of the model - the one we are migrating from
   * @tparam M2 the final version of the model - the one we are migrating to
   * @param typesafeConfig1 the typesafe configuration for the initial version of the model.
   * defaults to typesafe config's `ConfigFactory.load()`
   * @param typesafeConfig2 the typesafe configuration for the final version of the model.
   * defaults to typesafe config's `ConfigFactory.load()`
   */
  def builder[M1 : ModelType, M2 : ModelType](
    typesafeConfig1: Config = ConfigFactory.load(),
    typesafeConfig2: Config = ConfigFactory.load()): Builder[M1, M2] =
    builder(
      LongevityConfig.fromTypesafeConfig(typesafeConfig1),
      LongevityConfig.fromTypesafeConfig(typesafeConfig2))

  /** creates a migration builder
   *
   * @tparam M1 the initial version of the model - the one we are migrating from
   * @tparam M2 the final version of the model - the one we are migrating to
   * @param config1 the configuration for the initial version of the model
   * @param config2 the typesafe configuration for the final version of the model
   */
  def builder[M1 : ModelType, M2 : ModelType](
    config1: LongevityConfig,
    config2: LongevityConfig): Builder[M1, M2] =
    new Builder(config1, config2)

  /** a migration builder. the builder initially has no steps. add steps with methods [[create]],
   * [[drop]], and [[update]]. once all the steps are added, build the
   * [[Migration]] with method [[build]].
   * 
   * @tparam M1 the initial version of the model - the one we are migrating from
   * @tparam M2 the final version of the model - the one we are migrating to
   * @param config1 the configuration for the initial version of the model
   * @param config2 the typesafe configuration for the final version of the model
   */
  class Builder[M1 : ModelType, M2 : ModelType](
    config1: LongevityConfig,
    config2: LongevityConfig) {

    private var steps = Vector[MigrationStep[M1, M2]]()

    /** adds a [[CreateStep]] to the migration steps
     * @tparam P2 the persistent type to add to the model
     * @return this builder
     */
    def create[P2 : PEv[M2, ?]]: Builder[M1, M2] = {
      steps :+= CreateStep[M1, M2, P2]()
      this
    }

    /** adds a [[DropStep]] to the migration steps
     * @tparam P1 the persistent type to drop from the model
     * @return this builder
     */
    def drop[P1 : PEv[M1, ?]]: Builder[M1, M2] = {
      steps :+= DropStep[M1, M2, P1]()
      this
    }

    /** adds a [[UpdateStep]] to the migration steps
     * @tparam P1 the persistent type to migrate from the initial model
     * @tparam P2 the persistent type to migrat to the final model
     * @param f a function to migrate a single persistent object from type `P1` to type `P2`
     * @return this builder
     */
    def update[P1 : PEv[M1, ?], P2 : PEv[M2, ?]](f: P1 => P2): Builder[M1, M2] = {
      steps :+= UpdateStep[M1, M2, P1, P2](f)
      this
    }

    /** creates and returns the migration */
    def build: Migration[M1, M2] = new Migration(config1, config2, steps)

  }

}

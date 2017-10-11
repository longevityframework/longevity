package longevity.migrations

import com.typesafe.config.{Config, ConfigFactory}
import longevity.config.LongevityConfig
import longevity.model.{DerivedPType, ModelType, PEv, PType}

/** describes a migration from one version of a domain model to another
 *
 * @tparam M1 the initial version of the model - the one we are migrating from
 * @tparam M2 the final version of the model - the one we are migrating to
 * 
 * @param version1 the name of the version of the initial domain model. `None` indicates the initial
 * domain model is unversioned. (the domain model will be unversioned if this is the first time you
 * are doing a migration on this model.)
 * @param version2 the name of the version of the final domain model
 * @param config1 the configuration for the initial version of the model
 * @param config2 the configuration for the final version of the model
 * @param steps the steps to perform to complete the migration
 */
class Migration[M1 : ModelType, M2: ModelType] private (
  val version1: Option[String], 
  val version2: String, 
  val config1: LongevityConfig, 
  val config2: LongevityConfig, 
  val steps: Seq[MigrationStep[M1, M2]]) {

  private[migrations] val modelType1 = implicitly[ModelType[M1]]
  private[migrations] val modelType2 = implicitly[ModelType[M2]]

  /** checks if the migration is valid. returns a summary of the validity check */
  def validate: ValidationResult = {
    val (initialPTypes, finalPTypes) = pTypesFromSteps
    val errors = validationErrors(initialPTypes, finalPTypes)
    new ValidationResult(errors)
  }

  private def pTypesFromSteps: (Set[PType[M1, _]], Set[PType[M2, _]]) = {
    steps.foldLeft(
      (Set[PType[M1, _]](), Set[PType[M2, _]]())) {
      case ((initialPTypes, finalPTypes), step) =>
        step match {
          case s: DropStep[M1, M2, _] =>
            val pType1 = modelType1.pTypePool(s.pEv1.key)
            (initialPTypes + pType1, finalPTypes)
          case s: CreateStep[M1, M2, _] =>
            val pType2 = modelType2.pTypePool(s.pEv2.key)
            (initialPTypes, finalPTypes + pType2)
          case s: UpdateStep[M1, M2, _, _] =>
            val pType1 = modelType1.pTypePool(s.pEv1.key)
            val pType2 = modelType2.pTypePool(s.pEv2.key)
            (initialPTypes + pType1, finalPTypes + pType2)
        }
    }
  }

  private def validationErrors(initialPTypes: Set[PType[M1, _]], finalPTypes: Set[PType[M2, _]]) = {
    val missingInitialPTypes = nonDerivedPTypesFromModel[M1] -- initialPTypes
    val missingFinalPTypes = nonDerivedPTypesFromModel[M2] -- finalPTypes
    val initialMissings = missingInitialPTypes.map { pType => new InitialPersistentMissing(pType.name) }
    val finalMissings = missingFinalPTypes.map { pType => new FinalPersistentMissing(pType.name) }
    val initialDeriveds = filterDerived(initialPTypes).map { pType => new InitialDerivedPresent(pType.name) }
    val finalDeriveds = filterDerived(finalPTypes).map { pType => new FinalDerivedPresent(pType.name) }
    (initialMissings ++ finalMissings ++ initialDeriveds ++ finalDeriveds).toSeq
  }

  private def nonDerivedPTypesFromModel[M : ModelType]: Set[PType[M, _]] =
    implicitly[ModelType[M]].pTypePool.values.filter(!_.isInstanceOf[DerivedPType[_, _, _]]).toSet

  private def filterDerived[M](all: Set[PType[M, _]]) = all.filter(_.isInstanceOf[DerivedPType[_, _, _]]) 

}

/** contains methods for creating a migration builder */
object Migration {

  /** creates a migration builder
   *
   * @tparam M1 the initial version of the model - the one we are migrating from
   * @tparam M2 the final version of the model - the one we are migrating to
   * 
   * @param version1 the name of the version of the initial domain model. `None` indicates the initial
   * domain model is unversioned. (the domain model will be unversioned if this is the first time you
   * are doing a migration on this model.)
   * @param version2 the name of the version of the final domain model
   * @param typesafeConfig1 the typesafe configuration for the initial version of the model.
   * defaults to typesafe config's `ConfigFactory.load()`
   * @param typesafeConfig2 the typesafe configuration for the final version of the model.
   * defaults to typesafe config's `ConfigFactory.load()`
   */
  def builder[M1 : ModelType, M2 : ModelType](
    version1: Option[String],
    version2: String,
    typesafeConfig1: Config = ConfigFactory.load(),
    typesafeConfig2: Config = ConfigFactory.load()): Builder[M1, M2] =
    builder(version1, version2,
      LongevityConfig.fromTypesafeConfig(typesafeConfig1),
      LongevityConfig.fromTypesafeConfig(typesafeConfig2))

  /** creates a migration builder
   *
   * @tparam M1 the initial version of the model - the one we are migrating from
   * @tparam M2 the final version of the model - the one we are migrating to
   * 
   * @param version1 the name of the version of the initial domain model. `None` indicates the initial
   * domain model is unversioned. (the domain model will be unversioned if this is the first time you
   * are doing a migration on this model.)
   * @param version2 the name of the version of the final domain model
   * @param config1 the configuration for the initial version of the model
   * @param config2 the typesafe configuration for the final version of the model
   */
  def builder[M1 : ModelType, M2 : ModelType](
    version1: Option[String],
    version2: String,
    config1: LongevityConfig,
    config2: LongevityConfig): Builder[M1, M2] =
    new Builder(version1, version2, config1, config2)

  /** a migration builder. the builder initially has no steps. add steps with methods [[create]],
   * [[drop]], and [[update]]. once all the steps are added, build the
   * [[Migration]] with method [[build]].
   * 
   * @tparam M1 the initial version of the model - the one we are migrating from
   * @tparam M2 the final version of the model - the one we are migrating to
   * @param version1 the name of the version of the initial domain model. `None` indicates the initial
   * domain model is unversioned. (the domain model will be unversioned if this is the first time you
   * are doing a migration on this model.)
   * @param version2 the name of the version of the final domain model
   * @param config1 the configuration for the initial version of the model
   * @param config2 the typesafe configuration for the final version of the model
   */
  class Builder[M1 : ModelType, M2 : ModelType](
    version1: Option[String],
    version2: String,
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
    def build: Migration[M1, M2] = new Migration(version1, version2, config1, config2, steps)

  }

}

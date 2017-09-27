package longevity.migrations

// TODO scaladoc

/** a validation error that occur in a migration */
sealed trait ValidationError {

  /** a user-oriented description of the error */
  def message: String

}

/** a validation error indicating that the migration is missing a persistent from the initial domain
 * model
 *
 * @param name the name of the missing persistent
 */
class InitialPersistentMissing(val name: String) extends ValidationError {
  def message = s"The migration is missing a migration from the persistent type $name in the initial model"
}

/** a validation error indicating that the migration is missing a persistent from the final domain
 * model
 * 
 * @param name the name of the missing persistent
 */
class FinalPersistentMissing(val name: String) extends ValidationError {
  def message = s"The migration is missing a migration to the persistent type $name in the final model"
}

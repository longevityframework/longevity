package longevity.migrations

/** the result of validating a [[Migration]] */
class ValidationResult private[migrations] (val errors: Seq[ValidationError]) {

  /** returns true whenever the migration is valid */
  def isValid = errors.isEmpty

}

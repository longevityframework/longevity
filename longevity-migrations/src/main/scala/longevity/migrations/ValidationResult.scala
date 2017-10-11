package longevity.migrations

/** the result of validating a [[Migration]] */
class ValidationResult private[migrations] (val errors: Seq[ValidationError]) {

  /** returns true whenever the migration is valid */
  def isValid = errors.isEmpty

  /** returns `None` whenever the migration is valid. otherwise returns `Some` [[ValidationException]]. */
  def exception = if (isValid) None else Some(new ValidationException(errors))
  
}

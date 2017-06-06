package longevity.exceptions.model

/** thrown on attempt to construct a [[longevity.model.ModelType ModelType]] with more than one
 * [[longevity.model.PType PType]] for a single kind of persistent object
 */
class DuplicatePTypesException
extends ModelTypeException("a ModelType cannot contain multiple PTypes for the same Persistent")

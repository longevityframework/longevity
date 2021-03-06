package longevity.exceptions.model

/** thrown on attempt to construct a [[longevity.model.ModelType ModelType]] with more than one
 * [[longevity.model.CType CType]] for a single kind of component
 */
class DuplicateCTypesException
extends ModelTypeException("a ModelType cannot contain multiple CTypes for the same component")

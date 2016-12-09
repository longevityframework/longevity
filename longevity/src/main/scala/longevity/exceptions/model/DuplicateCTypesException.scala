package longevity.exceptions.model

/** thrown on attempt to construct a
 * [[longevity.model.CTypePool CTypePool]] with more than one
 * [[longevity.model.CType CType]] for a single kind of
 * component
 */
class DuplicateCTypesException
extends DomainModelException(
  "an CTypePool cannot contain multiple CTypes for the same component")

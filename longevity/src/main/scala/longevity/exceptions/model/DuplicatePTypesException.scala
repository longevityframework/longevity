package longevity.exceptions.model

/** thrown on attempt to construct a
 * [[longevity.model.PTypePool PTypePool]] with more than one
 * [[longevity.model.PType PType]] for a single kind of
 * persistent object
 */
class DuplicatePTypesException
extends DomainModelException(
  "an PTypePool cannot contain multiple PTypes for the same Persistent")

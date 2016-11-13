package longevity.exceptions.subdomain

/** thrown on attempt to construct a
 * [[longevity.subdomain.PTypePool PTypePool]] with more than one
 * [[longevity.subdomain.PType PType]] for a single kind of
 * persistent object
 */
class DuplicatePTypesException
extends SubdomainException(
  "an PTypePool cannot contain multiple PTypes for the same Persistent")

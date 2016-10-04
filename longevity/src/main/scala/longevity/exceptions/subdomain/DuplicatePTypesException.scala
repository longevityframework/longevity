package longevity.exceptions.subdomain

/** thrown on attempt to construct a
 * [[longevity.subdomain.ptype.PTypePool PTypePool]] with more than one
 * [[longevity.subdomain.ptype.PType PType]] for a single kind of
 * [[longevity.subdomain.persistent.Persistent persistent object]]
 */
class DuplicatePTypesException
extends SubdomainException(
  "an PTypePool cannot contain multiple PTypes for the same Persistent")

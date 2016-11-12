package longevity.exceptions.subdomain

/** thrown on attempt to construct a
 * [[longevity.subdomain.CTypePool CTypePool]] with more than one
 * [[longevity.subdomain.CType CType]] for a single kind of
 * component
 */
class DuplicateCTypesException
extends SubdomainException(
  "an CTypePool cannot contain multiple CTypes for the same component")

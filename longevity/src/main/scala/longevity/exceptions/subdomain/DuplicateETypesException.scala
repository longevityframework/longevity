package longevity.exceptions.subdomain

/** thrown on attempt to construct a
 * [[longevity.subdomain.ETypePool ETypePool]] with more than one
 * [[longevity.subdomain.EType EType]] for a single kind of
 * component
 */
class DuplicateETypesException
extends SubdomainException(
  "an ETypePool cannot contain multiple ETypes for the same component")

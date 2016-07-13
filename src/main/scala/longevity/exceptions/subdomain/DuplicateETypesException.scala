package longevity.exceptions.subdomain

/** thrown on attempt to construct a
 * [[longevity.subdomain.embeddable.ETypePool ETypePool]] with more than one
 * [[longevity.subdomain.embeddable.EType EType]] for a single kind of
 * [[longevity.subdomain.embeddable.Embeddable Embeddable]]
 */
class DuplicateETypesException
extends SubdomainException(
  "an ETypePool cannot contain multiple EntityTypes for the same Entity")

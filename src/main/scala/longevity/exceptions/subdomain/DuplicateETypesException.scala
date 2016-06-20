package longevity.exceptions.subdomain

/** thrown on attempt to construct a
 * [[longevity.subdomain.embeddable.ETypePool]] with more than one
 * [[longevity.subdomain.embeddable.EntityType]] for a single kind of
 * [[longevity.subdomain.embeddable.Entity]]
 */
class DuplicateETypesException
extends SubdomainException(
  "an ETypePool cannot contain multiple EntityTypes for the same Entity")

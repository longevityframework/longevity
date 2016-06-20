package longevity.exceptions.subdomain

/** thrown on attempt to construct a
 * [[longevity.subdomain.embeddable.EntityTypePool]] with more than one
 * [[longevity.subdomain.embeddable.EntityType]] for a single kind of
 * [[longevity.subdomain.embeddable.Entity]]
 */
class DuplicateEntityTypesException
extends SubdomainException(
  "an EntityTypePool cannot contain multiple EntityTypes for the same Entity")

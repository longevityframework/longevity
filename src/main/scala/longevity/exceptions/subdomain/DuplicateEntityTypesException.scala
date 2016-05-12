package longevity.exceptions.subdomain

/** thrown on attempt to construct a [[longevity.subdomain.entity.EntityTypePool]] with more than
 * one [[longevity.subdomain.entity.EntityType]] for the same [[longevity.subdomain.entity.Entity]] type.
 */
class DuplicateEntityTypesException
extends SubdomainException(
  "an EntityTypePool cannot contain multiple EntityTypes for the same Entity")

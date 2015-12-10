package longevity.exceptions.subdomain

/** thrown on attempt to construct a [[longevity.subdomain.EntityTypePool]] with more than
 * one [[longevity.subdomain.EntityType]] for the same [[longevity.subdomain.Entity]] type.
 */
class DuplicateEntityTypesException
extends SubdomainException(
  "an EntityTypePool cannot contain multiple EntityTypes for the same Entity")

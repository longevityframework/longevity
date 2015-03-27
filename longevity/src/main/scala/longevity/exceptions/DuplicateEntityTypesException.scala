package longevity.exceptions

/** An exception that is thrown on attempt to construct a [[longevity.subdomain.EntityTypePool]] with more than
 * one [[longevity.subdomain.EntityType]] for the same [[longevity.subdomain.Entity]] type. */
class DuplicateEntityTypesException
extends LongevityException("an EntityTypePool cannot contain multiple EntityTypes for the same Entity")

package longevity.exceptions

/** An exception that is thrown on attempt to construct a [[longevity.domain.EntityTypePool]] with more than
 * one [[longevity.domain.EntityType]] for the same [[longevity.domain.Entity]] type. */
class DuplicateEntityTypesException
extends Exception("a EntityTypePool cannot contain multiple EntityTypes for the same Entity")

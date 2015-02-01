package longevity.exceptions

/** An exception that is thrown on attempt to construct a [[EntityTypePool]] with more than one [[EntityType]]
 * for the same [[Entity]] type. */
class DuplicateEntityTypesException
extends Exception("a EntityTypePool cannot contain multiple EntityTypes for the same Entity")

package longevity.exceptions

/** An exception that is thrown on attempt to construct a [[ShorthandPool]] with more than one [[Shorthand]]
 * for the same `Actual` type.
 */
class DuplicateShorthandsException
extends ShorthandException("a ShorthandPool cannot contain multiple Shorthands with the same Long type")

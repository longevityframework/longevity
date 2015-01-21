package emblem.exceptions

/** An exception that is thrown on attempt to construct a [[ShorthandPool]] with more than one [[Shorthand]]
 * for the same `Long` type. */
class DuplicateShorthandsException
extends Exception("a ShorthandPool cannot contain multiple Shorthands with the same Long type")

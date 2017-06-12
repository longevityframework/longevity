package longevity.emblem.exceptions

/** An exception that is thrown on attempt to construct a
 * [[emblem.emblematic.UnionPool UnionPool]] with more than one
 * [[emblem.emblematic.Union Union]] for the same type
 */
private[longevity] class DuplicateUnionsException
extends UnionPoolException(
  "a UnionPool cannot contain multiple Unions for the same type")

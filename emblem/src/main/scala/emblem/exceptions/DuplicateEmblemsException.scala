package emblem.exceptions

/** An exception that is thrown on attempt to construct a
 * [[emblem.emblematic.EmblemPool EmblemPool]] with more than one
 * [[emblem.emblematic.Emblem Emblem]] for the same type
 */
class DuplicateEmblemsException
extends EmblemPoolException(
  "an EmblemPool cannot contain multiple Emblems for the same type")

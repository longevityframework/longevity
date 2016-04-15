package emblem.exceptions

/** An exception that is thrown on attempt to construct a [[EmblemPool]] with
 * more than one [[Emblem]] for the same [[HasEmblem]] type
 */
class DuplicateEmblemsException
extends EmblemPoolException(
  "an EmblemPool cannot contain multiple Emblems for the same HasEmblem type")

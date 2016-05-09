package emblem.exceptions

/** an exception that is thrown on attempt to construct a
 * [[emblem.emblematic.ExtractorPool ExtractorPool]] with more than one
 * [[emblem.emblematic.Extractor Extractor]] for the same `Domain` type
 */
class DuplicateExtractorsException
extends ExtractorPoolException(
  "an ExtractorPool cannot contain multiple Extractors with the same Range type")

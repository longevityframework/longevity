package emblem.exceptions

/** an exception that is thrown on attempt to construct a [[ExtractorPool]] with
 * more than one [[Extractor]] for the same `Domain` type.
 */
class DuplicateExtractorsException
extends ExtractorPoolException(
  "an ExtractorPool cannot contain multiple Extractors with the same Range type")

package emblem.exceptions

/** an exception that is thrown on attempt to construct a [[ExtractorPool]] with more than one [[Extractor]]
 * for the same `Range` type.
 */
class DuplicateExtractorsException
extends ExtractorPoolException("a ExtractorPool cannot contain multiple Extractors with the same Range type")

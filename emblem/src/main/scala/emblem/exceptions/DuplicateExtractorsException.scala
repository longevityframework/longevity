package emblem.exceptions

/** An exception that is thrown on attempt to construct a [[ExtractorPool]] with more than one [[Extractor]]
 * for the same `Actual` type.
 */
class DuplicateExtractorsException
extends Exception("a ExtractorPool cannot contain multiple Extractors with the same Actual type")

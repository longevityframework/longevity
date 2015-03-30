package emblem

import emblem.exceptions.DuplicateExtractorsException

object ExtractorPool {

  /** Collects a sequence of [[Extractor extractors]] into a [[ExtractorPool]].
   * @param extractors the sequence of extractors stored in the pool
   * @throws emblem.exceptions.DuplicateExtractorsException when two or more of the extractors have the same
   * Actual type
   */
  def apply(extractors: Extractor[_, _]*): ExtractorPool = {
    val actualTypeKeyMap: ExtractorPool = extractors.foldLeft(TypeKeyMap[Any, ExtractorFor]()) {
      case (map, extractor) => map + (extractor.actualTypeKey -> extractor)
    }
    if (extractors.size != actualTypeKeyMap.size) throw new DuplicateExtractorsException
    actualTypeKeyMap
  }

}

package emblem

import emblem.exceptions.DuplicateExtractorsException

object ExtractorPool {

  /** collects a sequence of [[Extractor extractors]] into a [[ExtractorPool]]
   * @param extractors the sequence of extractors stored in the pool
   * @throws emblem.exceptions.DuplicateExtractorsException when two or more of the extractors have the same
   * Range type
   */
  def apply(extractors: Extractor[_, _]*): ExtractorPool = {
    val rangeTypeKeyMap: ExtractorPool = extractors.foldLeft(TypeKeyMap[Any, ExtractorFor]()) {
      case (map, extractor) => map + (extractor.rangeTypeKey -> extractor)
    }
    if (extractors.size != rangeTypeKeyMap.size) throw new DuplicateExtractorsException
    rangeTypeKeyMap
  }

  /** an empty extractor pool */
  val empty: ExtractorPool = TypeKeyMap[Any, ExtractorFor]

}

package emblem.emblematic

import emblem.TypeKeyMap
import emblem.exceptions.DuplicateExtractorsException

object ExtractorPool {

  /** collects a sequence of [[Extractor extractors]] into an [[ExtractorPool]]
   * 
   * @param extractors the sequence of extractors stored in the pool
   * @throws emblem.exceptions.DuplicateExtractorsException when two or more of
   * the extractors have the same `Domain` type
   */
  def apply(extractors: Extractor[_, _]*): ExtractorPool = {
    val domainTypeKeyMap: ExtractorPool = extractors.foldLeft(TypeKeyMap[Any, ExtractorFor]()) {
      case (map, extractor) => map + (extractor.domainTypeKey -> extractor)
    }
    if (extractors.size != domainTypeKeyMap.size) throw new DuplicateExtractorsException
    domainTypeKeyMap
  }

  /** an empty extractor pool */
  val empty: ExtractorPool = TypeKeyMap[Any, ExtractorFor]

}

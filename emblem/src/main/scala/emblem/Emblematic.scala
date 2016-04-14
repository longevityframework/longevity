package emblem

/** describes a collection of types used in composite data structures.
 * along with all of the [[basicTypes basic types]] and supported collections
 * (`Option`, `Set`, and `Map`), a collection of [[Emblem emblems]] and
 * [[Extractor extractors]] describe case classes found in the
 * data structures.
 *
 * @param emblems the emblems to use in the traversal. defaults to empty
 * @param extractors the extractors to use in the traversal. defaults to empty
 */
case class Emblematic(
  emblems: EmblemPool = EmblemPool.empty,
  extractors: ExtractorPool = ExtractorPool.empty)

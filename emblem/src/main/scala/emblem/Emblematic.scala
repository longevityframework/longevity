package emblem

/** describes a collection of types used in composite data structures. these
 * data structures can contain all of the following types:
 * 
 * - all of the [[basicTypes basic types]]
 * - supported collections. currently `Option`, `Set`, and `Map`
 * - case classes represented in the collection of [[Emblem emblems]]
 * - case classes represented in the collection of [[Extractor extractors]]
 * - traits represented in the collection of [[Union unions]]
 *
 * @param emblems the emblems to use in the emblematic. defaults to empty
 * @param extractors the extractors to use in the emblematic. defaults to empty
 * @param unions the unions to use in the emblematic. defaults to empty
 */
case class Emblematic(
  emblems: EmblemPool = EmblemPool.empty,
  extractors: ExtractorPool = ExtractorPool.empty,
  unions: UnionPool = UnionPool.empty)

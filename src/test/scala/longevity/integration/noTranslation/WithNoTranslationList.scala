package longevity.integration.noTranslation

import longevity.subdomain._

case class WithNoTranslationList(
  uri: String,
  noTranslationList: List[NoTranslation])
extends Root

object WithNoTranslationList extends RootType[WithNoTranslationList] {
  val keySet = emptyKeySet
  val indexSet = emptyIndexSet
}

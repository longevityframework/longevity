package longevity.integration.noTranslation

import longevity.model.PType

case class WithNoTranslationList(
  uri: String,
  noTranslationList: List[NoTranslation])

object WithNoTranslationList extends PType[WithNoTranslationList] {
  object props {
  }
  val keySet = emptyKeySet
}

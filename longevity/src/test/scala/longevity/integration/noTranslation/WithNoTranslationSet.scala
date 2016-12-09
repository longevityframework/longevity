package longevity.integration.noTranslation

import longevity.model.PType

case class WithNoTranslationSet(
  uri: String,
  noTranslationSet: Set[NoTranslation])

object WithNoTranslationSet extends PType[WithNoTranslationSet] {
  object props {
  }
  val keySet = emptyKeySet
}

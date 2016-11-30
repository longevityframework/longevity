package longevity.integration.noTranslation

import longevity.subdomain.PType

case class WithNoTranslationOption(
  uri: String,
  noTranslationOption: Option[NoTranslation])

object WithNoTranslationOption extends PType[WithNoTranslationOption] {
  object props {
  }
  val keySet = emptyKeySet
}

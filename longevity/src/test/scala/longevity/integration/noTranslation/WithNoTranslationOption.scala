package longevity.integration.noTranslation

import longevity.model.PType

case class WithNoTranslationOption(
  uri: String,
  noTranslationOption: Option[NoTranslation])

object WithNoTranslationOption extends PType[DomainModel, WithNoTranslationOption] {
  object props {
  }
}

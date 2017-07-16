package longevity.integration.noTranslation

import longevity.model.PType
import longevity.model.annotations.pEv

case class WithNoTranslationOption(
  uri: String,
  noTranslationOption: Option[NoTranslation])

@pEv object WithNoTranslationOption extends PType[DomainModel, WithNoTranslationOption] {
  object props {
  }
}

package longevity.integration.noTranslation

import longevity.model.PType
import longevity.model.annotations.pEv

case class WithNoTranslationSet(
  uri: String,
  noTranslationSet: Set[NoTranslation])

@pEv object WithNoTranslationSet extends PType[DomainModel, WithNoTranslationSet] {
  object props {
  }
}

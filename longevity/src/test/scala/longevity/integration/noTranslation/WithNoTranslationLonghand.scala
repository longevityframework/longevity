package longevity.integration.noTranslation

import longevity.model.PType
import longevity.model.annotations.pEv

case class WithNoTranslationLonghand(
  uri: String,
  noTranslationLonghand: NoTranslationLonghand)

@pEv object WithNoTranslationLonghand extends PType[DomainModel, WithNoTranslationLonghand] {
  object props {
  }
}

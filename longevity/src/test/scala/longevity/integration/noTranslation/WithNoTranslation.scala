package longevity.integration.noTranslation

import longevity.model.PType
import longevity.model.annotations.pEv

case class WithNoTranslation(
  uri: String,
  noTranslation: NoTranslation)

@pEv object WithNoTranslation extends PType[DomainModel, WithNoTranslation] {
  object props {
  }
}

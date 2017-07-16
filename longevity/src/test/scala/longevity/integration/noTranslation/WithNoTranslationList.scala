package longevity.integration.noTranslation

import longevity.model.PType
import longevity.model.annotations.pEv

case class WithNoTranslationList(
  uri: String,
  noTranslationList: List[NoTranslation])

@pEv object WithNoTranslationList extends PType[DomainModel, WithNoTranslationList] {
  object props {
  }
}

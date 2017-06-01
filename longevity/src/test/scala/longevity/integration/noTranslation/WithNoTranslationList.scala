package longevity.integration.noTranslation

import longevity.model.PType

case class WithNoTranslationList(
  uri: String,
  noTranslationList: List[NoTranslation])

object WithNoTranslationList extends PType[DomainModel, WithNoTranslationList] {
  object props {
  }
  lazy val keySet = emptyKeySet
}

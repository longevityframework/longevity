package longevity.integration.noTranslation

import longevity.model.PType

case class WithNoTranslationLonghand(
  uri: String,
  noTranslationLonghand: NoTranslationLonghand)

object WithNoTranslationLonghand extends PType[DomainModel, WithNoTranslationLonghand] {
  object props {
  }
  lazy val keySet = emptyKeySet
}

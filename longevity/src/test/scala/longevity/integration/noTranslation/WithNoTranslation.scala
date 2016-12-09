package longevity.integration.noTranslation

import longevity.model.PType

case class WithNoTranslation(
  uri: String,
  noTranslation: NoTranslation)

object WithNoTranslation extends PType[WithNoTranslation] {
  object props {
  }
  val keySet = emptyKeySet
}

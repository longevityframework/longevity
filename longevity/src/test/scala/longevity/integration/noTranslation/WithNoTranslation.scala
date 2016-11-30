package longevity.integration.noTranslation

import longevity.subdomain.PType

case class WithNoTranslation(
  uri: String,
  noTranslation: NoTranslation)

object WithNoTranslation extends PType[WithNoTranslation] {
  object props {
  }
  val keySet = emptyKeySet
}

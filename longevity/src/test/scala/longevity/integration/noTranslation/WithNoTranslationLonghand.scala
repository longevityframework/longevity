package longevity.integration.noTranslation

import longevity.subdomain.PType

case class WithNoTranslationLonghand(
  uri: String,
  noTranslationLonghand: NoTranslationLonghand)

object WithNoTranslationLonghand extends PType[WithNoTranslationLonghand] {
  object props {
  }
  val keySet = emptyKeySet
}

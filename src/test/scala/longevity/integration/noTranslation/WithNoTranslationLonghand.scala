package longevity.integration.noTranslation

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithNoTranslationLonghand(
  uri: String,
  noTranslationLonghand: NoTranslationLonghand)
extends Persistent

object WithNoTranslationLonghand extends PType[WithNoTranslationLonghand] {
  object props {
  }
  object keys {
  }
}

package longevity.integration.noTranslation

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.PType

case class WithNoTranslationLonghand(
  uri: String,
  noTranslationLonghand: NoTranslationLonghand)
extends Root

object WithNoTranslationLonghand extends PType[WithNoTranslationLonghand] {
  object props {
  }
  object keys {
  }
}

package longevity.integration.noTranslation

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class WithNoTranslationLonghand(
  uri: String,
  noTranslationLonghand: NoTranslationLonghand)
extends Root

object WithNoTranslationLonghand extends RootType[WithNoTranslationLonghand] {
  object props {
  }
  object keys {
  }
}

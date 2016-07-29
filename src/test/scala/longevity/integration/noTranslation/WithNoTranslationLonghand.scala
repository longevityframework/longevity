package longevity.integration.noTranslation

import longevity.subdomain.persistent.Root
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

package longevity.integration.noTranslation

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class WithNoTranslation(
  uri: String,
  noTranslation: NoTranslation)
extends Root

object WithNoTranslation extends RootType[WithNoTranslation] {
  object props {
  }
  object keys {
  }
}

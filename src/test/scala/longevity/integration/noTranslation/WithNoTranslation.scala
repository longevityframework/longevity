package longevity.integration.noTranslation

import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

case class WithNoTranslation(
  uri: String,
  noTranslation: NoTranslation)
extends Root

object WithNoTranslation extends PType[WithNoTranslation] {
  object props {
  }
  object keys {
  }
}

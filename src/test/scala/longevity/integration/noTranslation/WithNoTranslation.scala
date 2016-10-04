package longevity.integration.noTranslation

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithNoTranslation(
  uri: String,
  noTranslation: NoTranslation)
extends Persistent

object WithNoTranslation extends PType[WithNoTranslation] {
  object props {
  }
  object keys {
  }
}

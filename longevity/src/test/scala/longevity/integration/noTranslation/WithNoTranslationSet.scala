package longevity.integration.noTranslation

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithNoTranslationSet(
  uri: String,
  noTranslationSet: Set[NoTranslation])
extends Persistent

object WithNoTranslationSet extends PType[WithNoTranslationSet] {
  object props {
  }
  object keys {
  }
}

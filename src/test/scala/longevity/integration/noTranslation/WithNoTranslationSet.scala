package longevity.integration.noTranslation

import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

case class WithNoTranslationSet(
  uri: String,
  noTranslationSet: Set[NoTranslation])
extends Root

object WithNoTranslationSet extends PType[WithNoTranslationSet] {
  object props {
  }
  object keys {
  }
}

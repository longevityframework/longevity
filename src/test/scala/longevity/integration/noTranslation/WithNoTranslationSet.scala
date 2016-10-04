package longevity.integration.noTranslation

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class WithNoTranslationSet(
  uri: String,
  noTranslationSet: Set[NoTranslation])
extends Root

object WithNoTranslationSet extends RootType[WithNoTranslationSet] {
  object props {
  }
  object keys {
  }
}

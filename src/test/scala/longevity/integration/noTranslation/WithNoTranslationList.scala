package longevity.integration.noTranslation

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class WithNoTranslationList(
  uri: String,
  noTranslationList: List[NoTranslation])
extends Root

object WithNoTranslationList extends RootType[WithNoTranslationList] {
  object props {
  }
  object keys {
  }
}

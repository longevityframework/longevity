package longevity.integration.noTranslation

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.PType

case class WithNoTranslationList(
  uri: String,
  noTranslationList: List[NoTranslation])
extends Root

object WithNoTranslationList extends PType[WithNoTranslationList] {
  object props {
  }
  object keys {
  }
}

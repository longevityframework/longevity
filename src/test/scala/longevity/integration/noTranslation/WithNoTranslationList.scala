package longevity.integration.noTranslation

import longevity.subdomain.persistent.Root
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
  object indexes {
  }
}

package longevity.integration.noTranslation

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithNoTranslationList(
  uri: String,
  noTranslationList: List[NoTranslation])
extends Persistent

object WithNoTranslationList extends PType[WithNoTranslationList] {
  object props {
  }
  object keys {
  }
}

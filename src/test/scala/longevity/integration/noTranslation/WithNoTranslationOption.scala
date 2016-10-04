package longevity.integration.noTranslation

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class WithNoTranslationOption(
  uri: String,
  noTranslationOption: Option[NoTranslation])
extends Root

object WithNoTranslationOption extends RootType[WithNoTranslationOption] {
  object props {
  }
  object keys {
  }
}

package longevity.integration.noTranslation

import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

case class WithNoTranslationOption(
  uri: String,
  noTranslationOption: Option[NoTranslation])
extends Root

object WithNoTranslationOption extends PType[WithNoTranslationOption] {
  object props {
  }
  object keys {
  }
}

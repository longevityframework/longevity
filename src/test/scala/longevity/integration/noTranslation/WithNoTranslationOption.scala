package longevity.integration.noTranslation

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithNoTranslationOption(
  uri: String,
  noTranslationOption: Option[NoTranslation])
extends Persistent

object WithNoTranslationOption extends PType[WithNoTranslationOption] {
  object props {
  }
  object keys {
  }
}

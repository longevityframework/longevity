package longevity.integration.noTranslation

import longevity.subdomain._

case class WithNoTranslationOption(
  uri: String,
  noTranslationOption: Option[NoTranslation])
extends Root

object WithNoTranslationOption extends RootType[WithNoTranslationOption] {
  object keys {
  }
  object indexes {
  }
}

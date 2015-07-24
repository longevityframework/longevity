package longevity.integration.noTranslation

import longevity.subdomain._

case class WithNoTranslationOption(
  uri: String,
  noTranslationOption: Option[NoTranslation])
extends RootEntity

object WithNoTranslationOption extends RootEntityType[WithNoTranslationOption]

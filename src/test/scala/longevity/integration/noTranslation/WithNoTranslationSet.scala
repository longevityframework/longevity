package longevity.integration.noTranslation

import longevity.subdomain._

case class WithNoTranslationSet(
  uri: String,
  noTranslationSet: Set[NoTranslation])
extends RootEntity

object WithNoTranslationSet extends RootEntityType[WithNoTranslationSet]

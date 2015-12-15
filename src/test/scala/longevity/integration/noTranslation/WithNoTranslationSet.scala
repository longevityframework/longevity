package longevity.integration.noTranslation

import longevity.subdomain._

case class WithNoTranslationSet(
  uri: String,
  noTranslationSet: Set[NoTranslation])
extends Root

object WithNoTranslationSet extends RootType[WithNoTranslationSet]

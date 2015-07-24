package longevity.integration.noTranslation

import longevity.subdomain._

case class WithNoTranslationList(
  uri: String,
  noTranslationList: List[NoTranslation])
extends RootEntity

object WithNoTranslationList extends RootEntityType[WithNoTranslationList]

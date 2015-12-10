package longevity.integration.noTranslation

import longevity.subdomain._

case class WithNoTranslation(
  uri: String,
  noTranslation: NoTranslation)
extends RootEntity

object WithNoTranslation extends RootEntityType[WithNoTranslation]

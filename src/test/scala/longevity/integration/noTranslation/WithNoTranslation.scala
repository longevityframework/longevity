package longevity.integration.noTranslation

import longevity.subdomain._

case class WithNoTranslation(
  uri: String,
  noTranslation: NoTranslation)
extends Root

object WithNoTranslation extends RootType[WithNoTranslation]

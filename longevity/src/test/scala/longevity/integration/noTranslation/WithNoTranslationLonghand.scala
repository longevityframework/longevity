package longevity.integration.noTranslation

import longevity.subdomain._

case class WithNoTranslationLonghand(
  uri: String,
  noTranslationLonghand: NoTranslationLonghand)
extends RootEntity

object WithNoTranslationLonghand extends RootEntityType[WithNoTranslationLonghand]

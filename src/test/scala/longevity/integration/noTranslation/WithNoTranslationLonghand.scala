package longevity.integration.noTranslation

import longevity.subdomain._

case class WithNoTranslationLonghand(
  uri: String,
  noTranslationLonghand: NoTranslationLonghand)
extends Root

object WithNoTranslationLonghand extends RootType[WithNoTranslationLonghand]

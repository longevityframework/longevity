package longevity.integration

import longevity.context._
import longevity.subdomain._

/** a malformed subdomain that manages to include objects that don't have mongo transations. */
package object noTranslation {

  implicit val shorthandPool = ShorthandPool.empty

  object context {

    val entityTypes = EntityTypePool(
      WithNoTranslation,
      WithNoTranslationList,
      WithNoTranslationLonghand,
      WithNoTranslationOption,
      WithNoTranslationSet)

    val subdomain = Subdomain("No Translation", entityTypes)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}

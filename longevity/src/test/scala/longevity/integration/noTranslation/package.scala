package longevity.integration

import longevity.context._
import longevity.subdomain._

package object noTranslation {

  val entityTypes = EntityTypePool() +
    WithNoTranslation +
    WithNoTranslationList +
    WithNoTranslationLonghand +
    WithNoTranslationOption +
    WithNoTranslationSet

  val subdomain = Subdomain("No Translation", entityTypes)

  val booleanShorthand = Shorthand[NoTranslationLonghand, NoTranslation]

  val shorthandPool = ShorthandPool.empty + booleanShorthand

  val longevityContext = LongevityContext(subdomain, shorthandPool, Mongo)

}

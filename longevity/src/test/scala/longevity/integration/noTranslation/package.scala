package longevity.integration

import longevity.context._
import longevity.subdomain._

package object noTranslation {

  val booleanShorthand = Shorthand[NoTranslationLonghand, NoTranslation]

  implicit val shorthandPool = ShorthandPool.empty + booleanShorthand

  val entityTypes = EntityTypePool() +
    WithNoTranslation +
    WithNoTranslationList +
    WithNoTranslationLonghand +
    WithNoTranslationOption +
    WithNoTranslationSet

  val subdomain = Subdomain("No Translation", entityTypes)

  val longevityContext = LongevityContext(subdomain, shorthandPool, Mongo)

}

package longevity.integration

import longevity.context._
import longevity.persistence._
import longevity.subdomain._

/** covers a root entity with shorthands for every supported basic type */
package object allShorthands {

  val entityTypes = EntityTypePool() + AllShorthands

  val subdomain = Subdomain("All Shorthands", entityTypes)

  val booleanShorthand = Shorthand[BooleanShorthand, Boolean]
  val charShorthand = Shorthand[CharShorthand, Char]
  val doubleShorthand = Shorthand[DoubleShorthand, Double]
  val floatShorthand = Shorthand[FloatShorthand, Float]
  val intShorthand = Shorthand[IntShorthand, Int]
  val longShorthand = Shorthand[LongShorthand, Long]
  val stringShorthand = Shorthand[StringShorthand, String]

  val shorthandPool = ShorthandPool.empty +
    booleanShorthand +
    charShorthand +
    doubleShorthand +
    floatShorthand +
    intShorthand +
    longShorthand +
    stringShorthand

  val longevityContext = LongevityContext(subdomain, shorthandPool, Mongo)

}

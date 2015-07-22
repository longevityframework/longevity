package longevity.integration

import longevity.context._
import longevity.shorthands._
import longevity.subdomain._

/** covers everything found in the rest of the integration tests */
package object master {

  val entityTypes = EntityTypePool() +
    AllAttributes +
    Associated +
    AttributeLists +
    AttributeOptions +
    AttributeSets +
    Component +
    OneAttribute +
    OneShorthand +
    WithAssoc +
    WithAssocList +
    WithAssocOption +
    WithAssocSet +
    WithComponent +
    WithComponentOption

  val subdomain = Subdomain("Master", entityTypes)

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

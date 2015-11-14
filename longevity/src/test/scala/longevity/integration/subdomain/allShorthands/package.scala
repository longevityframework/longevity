package longevity.integration.subdomain

import com.github.nscala_time.time.Imports._
import longevity.context._
import longevity.subdomain._

/** covers a root entity with shorthands for every supported basic type */
package object allShorthands {

  val booleanShorthand = Shorthand[BooleanShorthand, Boolean]
  val charShorthand = Shorthand[CharShorthand, Char]
  val dateTimeShorthand = Shorthand[DateTimeShorthand, DateTime]
  val doubleShorthand = Shorthand[DoubleShorthand, Double]
  val floatShorthand = Shorthand[FloatShorthand, Float]
  val intShorthand = Shorthand[IntShorthand, Int]
  val longShorthand = Shorthand[LongShorthand, Long]
  val stringShorthand = Shorthand[StringShorthand, String]

  implicit val shorthandPool = ShorthandPool.empty +
    booleanShorthand +
    charShorthand +
    dateTimeShorthand +
    doubleShorthand +
    floatShorthand +
    intShorthand +
    longShorthand +
    stringShorthand

  object context {
    val entityTypes = EntityTypePool() + AllShorthands
    val subdomain = Subdomain("All Shorthands", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
  }

}

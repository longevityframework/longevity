package longevity.integration.subdomain

import com.github.nscala_time.time.Imports.DateTime
import longevity.context.LongevityContext
import longevity.context.Cassandra
import longevity.context.Mongo
import longevity.subdomain.Shorthand
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

package object allShorthands {

  object shorthands {

    val booleanShorthand = Shorthand[BooleanShorthand, Boolean]
    val charShorthand = Shorthand[CharShorthand, Char]
    val dateTimeShorthand = Shorthand[DateTimeShorthand, DateTime]
    val doubleShorthand = Shorthand[DoubleShorthand, Double]
    val floatShorthand = Shorthand[FloatShorthand, Float]
    val intShorthand = Shorthand[IntShorthand, Int]
    val longShorthand = Shorthand[LongShorthand, Long]
    val stringShorthand = Shorthand[StringShorthand, String]

    val shorthandPool = ShorthandPool.empty +
      booleanShorthand +
      charShorthand +
      dateTimeShorthand +
      doubleShorthand +
      floatShorthand +
      intShorthand +
      longShorthand +
      stringShorthand

  }

  val subdomain = Subdomain(
    "All Shorthands",
    PTypePool(AllShorthands),
    shorthandPool = shorthands.shorthandPool)

  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}

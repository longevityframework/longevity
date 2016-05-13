package longevity.integration.subdomain

import com.github.nscala_time.time.Imports.DateTime
import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.entity.EntityTypePool
import longevity.subdomain.Shorthand
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single component entity with shorthands for every supported basic type */
package object withComponentWithShorthands {

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

  object context {
    val subdomain = Subdomain(
      "With Component With Shorthands",
      PTypePool(WithComponentWithShorthands),
      EntityTypePool(ComponentWithShorthands),
      shorthands.shorthandPool)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}

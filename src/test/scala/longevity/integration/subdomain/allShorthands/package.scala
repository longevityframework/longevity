package longevity.integration.subdomain

import longevity.context.LongevityContext
import longevity.context.Cassandra
import longevity.context.Mongo
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

package object allShorthands {

  val subdomain = Subdomain(
    "All Shorthands",
    PTypePool(AllShorthands),
    shorthandPool = ShorthandPool(
      BooleanShorthand,
      CharShorthand,
      DateTimeShorthand,
      DoubleShorthand,
      FloatShorthand,
      IntShorthand,
      LongShorthand,
      StringShorthand))

  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}

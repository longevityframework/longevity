package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single component entity with shorthands for every supported basic type */
package object withComponentWithShorthands {

  val subdomain = Subdomain(
    "With Component With Shorthands",
    PTypePool(WithComponentWithShorthands),
    ETypePool(ComponentWithShorthands),
    ShorthandPool(
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

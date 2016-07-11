package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a single component entity */
package object componentWithList {

  val subdomain = Subdomain(
    "Component With List",
    PTypePool(WithComponentWithList),
    ETypePool(Component))
  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}

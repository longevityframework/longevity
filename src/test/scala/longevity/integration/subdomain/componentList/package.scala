package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a list of component entities */
package object componentList {

  val subdomain = Subdomain("Component List", PTypePool(WithComponentList), ETypePool(Component))
  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}

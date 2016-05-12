package longevity.integration.subdomain

import longevity.context.LongevityContext
import longevity.context.Cassandra
import longevity.context.Mongo
import longevity.subdomain.entity.EntityTypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a poly type and multiple derived types */
package object derivedEntities {

  object context {
    val subdomain = Subdomain(
      "Derived Entities",
      PTypePool(PolyRoot, FirstDerivedRoot, SecondDerivedRoot),
      EntityTypePool(PolyEntity, FirstDerivedEntity, SecondDerivedEntity))
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}

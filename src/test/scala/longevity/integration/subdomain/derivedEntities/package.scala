package longevity.integration.subdomain

import longevity.context.LongevityContext
import longevity.context.Cassandra
import longevity.context.Mongo
import longevity.subdomain._

/** covers a root entity with a poly type and multiple derived types */
package object derivedEntities {

  object context {
    val entityTypes = EntityTypePool(
      BaseEntity,
      BaseRoot,
      FirstDerivedEntity,
      FirstDerivedRoot,
      SecondDerivedEntity,
      SecondDerivedRoot)
    val subdomain = Subdomain("Derived Entities", entityTypes)
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}

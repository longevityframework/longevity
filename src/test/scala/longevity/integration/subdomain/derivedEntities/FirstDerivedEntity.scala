package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.DerivedType
import longevity.subdomain.EntityType

case class FirstDerivedEntity(
  uri: String,
  first: String)
extends BaseEntity

object FirstDerivedEntity
extends EntityType[FirstDerivedEntity]
with DerivedType[BaseEntity, FirstDerivedEntity] {
  val polyType = BaseEntity
}


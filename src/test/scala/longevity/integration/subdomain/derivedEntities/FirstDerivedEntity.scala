package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.DerivedType
import longevity.subdomain.EntityType

case class FirstDerivedEntity(
  uri: String,
  first: String)
extends PolyEntity

object FirstDerivedEntity
extends EntityType[FirstDerivedEntity]
with DerivedType[PolyEntity, FirstDerivedEntity] {
  val polyType = PolyEntity
}


package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.entity.DerivedType

case class FirstDerivedEntity(
  uri: String,
  first: String)
extends PolyEntity

object FirstDerivedEntity extends DerivedType[FirstDerivedEntity, PolyEntity] {
  val polyType = PolyEntity
}


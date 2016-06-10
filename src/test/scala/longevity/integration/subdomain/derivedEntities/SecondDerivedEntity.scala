package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.entity.DerivedType

case class SecondDerivedEntity(
  uri: String,
  second: String)
extends PolyEntity

object SecondDerivedEntity extends DerivedType[SecondDerivedEntity, PolyEntity] {
  val polyType = PolyEntity
}
package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.entity.DerivedType
import longevity.subdomain.entity.EntityType

case class SecondDerivedEntity(
  uri: String,
  second: String)
extends PolyEntity

object SecondDerivedEntity extends DerivedType[SecondDerivedEntity, PolyEntity] {
  val polyType = PolyEntity
}

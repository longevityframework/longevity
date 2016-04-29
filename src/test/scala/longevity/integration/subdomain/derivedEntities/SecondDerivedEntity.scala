package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.DerivedType
import longevity.subdomain.EntityType

case class SecondDerivedEntity(
  uri: String,
  second: String)
extends PolyEntity

object SecondDerivedEntity
extends EntityType[SecondDerivedEntity]
with DerivedType[PolyEntity, SecondDerivedEntity] {
  val polyType = PolyEntity
}

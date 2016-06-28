package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.embeddable.DerivedType

case class SecondDerivedEntity(
  id: PolyEntityId,
  second: String)
extends PolyEntity

object SecondDerivedEntity extends DerivedType[SecondDerivedEntity, PolyEntity] {
  val polyType = PolyEntity
}

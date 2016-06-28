package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.embeddable.DerivedType

case class FirstDerivedEntity(
  id: PolyEntityId,
  first: String)
extends PolyEntity

object FirstDerivedEntity extends DerivedType[FirstDerivedEntity, PolyEntity] {
  val polyType = PolyEntity
}


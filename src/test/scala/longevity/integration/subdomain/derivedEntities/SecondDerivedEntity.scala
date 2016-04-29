package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.DerivedType
import longevity.subdomain.EntityType

case class SecondDerivedEntity(
  uri: String,
  second: String)
extends BaseEntity

object SecondDerivedEntity
extends EntityType[SecondDerivedEntity]
with DerivedType[BaseEntity, SecondDerivedEntity] {
  val polyType = BaseEntity
}

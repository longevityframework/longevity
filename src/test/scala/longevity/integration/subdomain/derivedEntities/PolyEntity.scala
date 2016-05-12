package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.entity.PolyType
import longevity.subdomain.entity.Entity
import longevity.subdomain.entity.EntityType

trait PolyEntity extends Entity {
  val uri: String
}

object PolyEntity extends EntityType[PolyEntity] with PolyType[PolyEntity]

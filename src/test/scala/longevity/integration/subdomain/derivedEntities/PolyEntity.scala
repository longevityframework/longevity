package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.PolyType
import longevity.subdomain.Entity
import longevity.subdomain.EntityType

trait PolyEntity extends Entity {
  val uri: String
}

object PolyEntity extends EntityType[PolyEntity] with PolyType[PolyEntity]

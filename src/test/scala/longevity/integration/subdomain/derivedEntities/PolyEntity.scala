package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.embeddable.PolyType
import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

trait PolyEntity extends Entity {
  val uri: String
}

object PolyEntity extends EntityType[PolyEntity] with PolyType[PolyEntity]

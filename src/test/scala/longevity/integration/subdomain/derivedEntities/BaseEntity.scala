package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.BaseType
import longevity.subdomain.Entity
import longevity.subdomain.EntityType

trait BaseEntity extends Entity {
  val uri: String
}

object BaseEntity extends EntityType[BaseEntity] with BaseType[BaseEntity]

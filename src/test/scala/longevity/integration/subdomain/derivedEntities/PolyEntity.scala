package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.embeddable.Entity

trait PolyEntity extends Entity {
  val id: PolyEntityId
}

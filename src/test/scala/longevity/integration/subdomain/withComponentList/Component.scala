package longevity.integration.subdomain.withComponentList

import longevity.subdomain.entity.Entity
import longevity.subdomain.entity.EntityType

case class Component(uri: String) extends Entity

object Component extends EntityType[Component]

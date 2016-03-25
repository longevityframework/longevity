package longevity.integration.subdomain.withComponentList

import longevity.subdomain.Entity
import longevity.subdomain.EntityType

case class Component(uri: String) extends Entity

object Component extends EntityType[Component]

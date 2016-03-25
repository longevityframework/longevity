package longevity.integration.subdomain.withComponentSet

import longevity.subdomain.Entity
import longevity.subdomain.EntityType

case class Component(uri: String) extends Entity

object Component extends EntityType[Component]

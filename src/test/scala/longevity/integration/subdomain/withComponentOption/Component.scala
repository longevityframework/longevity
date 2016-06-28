package longevity.integration.subdomain.withComponentOption

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class Component(id: String) extends Entity

object Component extends EntityType[Component]

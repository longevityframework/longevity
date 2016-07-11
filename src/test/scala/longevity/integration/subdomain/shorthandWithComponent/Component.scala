package longevity.integration.subdomain.shorthandWithComponent

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class Component(id: String, tag: String) extends Entity

object Component extends EntityType[Component]

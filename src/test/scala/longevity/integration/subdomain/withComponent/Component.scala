package longevity.integration.subdomain.withComponent

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class Component(id: String, tags: Set[String]) extends Entity

object Component extends EntityType[Component]

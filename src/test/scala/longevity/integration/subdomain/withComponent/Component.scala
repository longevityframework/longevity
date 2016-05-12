package longevity.integration.subdomain.withComponent

import longevity.subdomain.entity.Entity
import longevity.subdomain.entity.EntityType

case class Component(uri: String, tags: Set[String]) extends Entity

object Component extends EntityType[Component]

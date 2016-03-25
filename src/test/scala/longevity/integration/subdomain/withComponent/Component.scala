package longevity.integration.subdomain.withComponent

import longevity.subdomain.Entity
import longevity.subdomain.EntityType

case class Component(uri: String, tags: Set[String]) extends Entity

object Component extends EntityType[Component]

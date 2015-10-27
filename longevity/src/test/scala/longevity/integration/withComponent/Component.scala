package longevity.integration.withComponent

import longevity.subdomain._

case class Component(uri: String, tags: Set[String]) extends Entity

object Component extends EntityType[Component]

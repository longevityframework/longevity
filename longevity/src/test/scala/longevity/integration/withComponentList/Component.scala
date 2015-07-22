package longevity.integration.withComponentList

import longevity.subdomain._

case class Component(uri: String) extends Entity

object Component extends EntityType[Component]

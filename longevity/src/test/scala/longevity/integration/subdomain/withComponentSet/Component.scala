package longevity.integration.subdomain.withComponentSet

import longevity.subdomain._

case class Component(uri: String) extends Entity

object Component extends EntityType[Component]

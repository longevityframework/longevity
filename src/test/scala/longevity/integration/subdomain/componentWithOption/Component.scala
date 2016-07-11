package longevity.integration.subdomain.componentWithOption

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class Component(id: String, tag: Option[String]) extends Entity

object Component extends EntityType[Component]

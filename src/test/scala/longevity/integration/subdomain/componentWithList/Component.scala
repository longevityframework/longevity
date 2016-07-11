package longevity.integration.subdomain.componentWithList

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class Component(id: String, tags: List[String]) extends Entity

object Component extends EntityType[Component]

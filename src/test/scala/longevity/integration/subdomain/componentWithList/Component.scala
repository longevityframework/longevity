package longevity.integration.subdomain.componentWithList

import longevity.subdomain.embeddable.Entity

case class Component(id: String, tags: List[String]) extends Entity

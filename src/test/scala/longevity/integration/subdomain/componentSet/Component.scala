package longevity.integration.subdomain.componentSet

import longevity.subdomain.embeddable.Entity

case class Component(id: String, tags: Set[String]) extends Entity

package longevity.integration.subdomain.componentOption

import longevity.subdomain.embeddable.Entity

case class Component(id: String, tags: Set[String]) extends Entity

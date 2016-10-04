package longevity.integration.subdomain.componentSet

import longevity.subdomain.embeddable.Embeddable

case class Component(id: String, tags: Set[String]) extends Embeddable

package longevity.integration.subdomain.componentWithSet

import longevity.subdomain.Embeddable

case class Component(id: String, tags: Set[String]) extends Embeddable

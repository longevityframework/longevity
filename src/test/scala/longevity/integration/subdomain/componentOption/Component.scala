package longevity.integration.subdomain.componentOption

import longevity.subdomain.embeddable.Embeddable

case class Component(id: String, tags: Set[String]) extends Embeddable

package longevity.integration.subdomain.componentList

import longevity.subdomain.embeddable.Embeddable

case class Component(id: String, tags: Set[String]) extends Embeddable

package longevity.integration.subdomain.componentList

import longevity.subdomain.Embeddable

case class Component(id: String, tags: Set[String]) extends Embeddable

package longevity.integration.subdomain.componentWithList

import longevity.subdomain.embeddable.Embeddable

case class Component(id: String, tags: List[String]) extends Embeddable

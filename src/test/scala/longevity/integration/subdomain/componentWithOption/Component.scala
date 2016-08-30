package longevity.integration.subdomain.componentWithOption

import longevity.subdomain.embeddable.Entity

case class Component(id: String, tag: Option[String]) extends Entity

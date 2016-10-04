package longevity.integration.subdomain.componentWithOption

import longevity.subdomain.embeddable.Embeddable

case class Component(id: String, tag: Option[String]) extends Embeddable

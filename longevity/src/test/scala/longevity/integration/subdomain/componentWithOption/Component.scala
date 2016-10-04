package longevity.integration.subdomain.componentWithOption

import longevity.subdomain.Embeddable

case class Component(id: String, tag: Option[String]) extends Embeddable

package longevity.integration.subdomain.componentWithOption

import longevity.subdomain.annotations.component

@component
case class Component(id: String, tag: Option[String])

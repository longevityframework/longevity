package longevity.integration.subdomain.componentWithOption

import longevity.model.annotations.component

@component
case class Component(id: String, tag: Option[String])

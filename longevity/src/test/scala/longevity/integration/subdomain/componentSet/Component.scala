package longevity.integration.subdomain.componentSet

import longevity.model.annotations.component

@component
case class Component(id: String, tags: Set[String])

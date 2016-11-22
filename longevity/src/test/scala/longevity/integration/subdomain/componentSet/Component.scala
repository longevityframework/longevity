package longevity.integration.subdomain.componentSet

import longevity.subdomain.annotations.component

@component
case class Component(id: String, tags: Set[String])

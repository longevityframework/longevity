package longevity.integration.subdomain.componentOption

import longevity.subdomain.annotations.component

@component
case class Component(id: String, tags: Set[String])

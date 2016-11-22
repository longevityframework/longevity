package longevity.integration.subdomain.componentWithSet

import longevity.subdomain.annotations.component

@component
case class Component(id: String, tags: Set[String])

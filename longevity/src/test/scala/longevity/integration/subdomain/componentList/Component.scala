package longevity.integration.subdomain.componentList

import longevity.subdomain.annotations.component

@component
case class Component(id: String, tags: Set[String])

package longevity.integration.subdomain.componentList

import longevity.model.annotations.component

@component
case class Component(id: String, tags: Set[String])

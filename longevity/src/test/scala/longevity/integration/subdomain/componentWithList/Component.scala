package longevity.integration.subdomain.componentWithList

import longevity.subdomain.annotations.component

@component
case class Component(id: String, tags: List[String])

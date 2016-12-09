package longevity.integration.model.componentSet

import longevity.model.annotations.component

@component
case class Component(id: String, tags: Set[String])

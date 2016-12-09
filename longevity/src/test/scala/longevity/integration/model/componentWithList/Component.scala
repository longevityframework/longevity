package longevity.integration.model.componentWithList

import longevity.model.annotations.component

@component
case class Component(id: String, tags: List[String])

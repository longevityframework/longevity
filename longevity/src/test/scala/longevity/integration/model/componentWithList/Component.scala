package longevity.integration.model.componentWithList

import longevity.model.annotations.component

@component[DomainModel]
case class Component(id: String, tags: List[String])

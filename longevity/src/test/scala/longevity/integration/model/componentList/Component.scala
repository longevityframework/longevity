package longevity.integration.model.componentList

import longevity.model.annotations.component

@component[DomainModel]
case class Component(id: String, tags: Set[String])

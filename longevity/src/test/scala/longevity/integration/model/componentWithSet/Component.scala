package longevity.integration.model.componentWithSet

import longevity.model.annotations.component

@component[DomainModel]
case class Component(id: String, tags: Set[String])

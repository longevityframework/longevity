package longevity.integration.model.shorthandWithComponent

import longevity.model.annotations.component

@component[DomainModel]
case class Component(id: String, tag: String)

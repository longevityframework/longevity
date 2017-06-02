package longevity.integration.model.componentWithOption

import longevity.model.annotations.component

@component[DomainModel]
case class Component(id: String, tag: Option[String])

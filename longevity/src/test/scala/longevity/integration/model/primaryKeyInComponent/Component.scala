package longevity.integration.model.primaryKeyInComponent

import longevity.model.annotations.component

@component[DomainModel]
case class Component(prop1: String, prop2: String, key: Key)

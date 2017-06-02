package longevity.integration.model.keyInComponent

import longevity.model.annotations.component

@component[DomainModel]
case class Component(prop1: String, prop2: String, key: Key)

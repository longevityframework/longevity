package longevity.integration.model.primaryKeyInComponentWithPartialPartition

import longevity.model.annotations.component

@component[DomainModel]
case class Component(prop1: String, prop2: String, key: Key)

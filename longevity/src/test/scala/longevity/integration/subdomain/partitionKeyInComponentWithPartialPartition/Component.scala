package longevity.integration.subdomain.partitionKeyInComponentWithPartialPartition

import longevity.model.annotations.component

@component
case class Component(prop1: String, prop2: String, key: Key)

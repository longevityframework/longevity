package longevity.integration.subdomain.partitionKeyInComponentWithPartialPartition

import longevity.subdomain.Embeddable

case class Component(prop1: String, prop2: String, key: Key) extends Embeddable

package longevity.integration.subdomain.partitionKeyWithComplexPartialPartition

import longevity.subdomain.Embeddable

case class SubKey(
  prop1: String,
  prop2: String)
extends Embeddable

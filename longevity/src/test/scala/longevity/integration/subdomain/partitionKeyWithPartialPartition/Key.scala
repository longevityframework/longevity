package longevity.integration.subdomain.partitionKeyWithPartialPartition

import longevity.subdomain.KeyVal

case class Key(
  prop1: String,
  prop2: String)
extends KeyVal[PartitionKeyWithPartialPartition]

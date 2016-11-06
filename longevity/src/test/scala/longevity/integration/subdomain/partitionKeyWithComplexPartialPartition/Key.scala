package longevity.integration.subdomain.partitionKeyWithComplexPartialPartition

import longevity.subdomain.KeyVal

case class Key(
  prop1: String,
  subKey: SubKey,
  prop2: String)
extends KeyVal[PartitionKeyWithComplexPartialPartition, Key]

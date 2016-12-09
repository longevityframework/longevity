package longevity.integration.subdomain.partitionKeyWithComplexPartialPartition

import longevity.model.annotations.keyVal

@keyVal[PartitionKeyWithComplexPartialPartition]
case class Key(
  prop1: String,
  subKey: SubKey,
  prop2: String)

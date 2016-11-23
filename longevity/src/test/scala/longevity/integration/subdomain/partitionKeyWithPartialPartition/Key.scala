package longevity.integration.subdomain.partitionKeyWithPartialPartition

import longevity.subdomain.annotations.keyVal

@keyVal[PartitionKeyWithPartialPartition]
case class Key(prop1: String, prop2: String)

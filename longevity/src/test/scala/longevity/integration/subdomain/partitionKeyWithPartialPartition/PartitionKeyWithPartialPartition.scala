package longevity.integration.subdomain.partitionKeyWithPartialPartition

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(partitionKey(props.key, partition(props.key.prop1))))
case class PartitionKeyWithPartialPartition(
  key: Key)

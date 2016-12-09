package longevity.integration.model.partitionKeyWithPartialPartition

import longevity.model.annotations.persistent

@persistent(keySet = Set(partitionKey(props.key, partition(props.key.prop1))))
case class PartitionKeyWithPartialPartition(
  key: Key)

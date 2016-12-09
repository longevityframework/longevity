package longevity.integration.model.partitionKeyWithComplexPartialPartition

import longevity.model.annotations.persistent

@persistent(keySet = Set(
  partitionKey(props.key, partition(props.key.prop1, props.key.subKey.prop1))))
case class PartitionKeyWithComplexPartialPartition(key: Key)

package longevity.integration.subdomain.partitionKeyWithComplexPartialPartition

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(
  partitionKey(props.key, partition(props.key.prop1, props.key.subKey.prop1))))
case class PartitionKeyWithComplexPartialPartition(key: Key)

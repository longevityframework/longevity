package longevity.integration.model.partitionKeyWithMultipleProperties

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.key)))
case class PartitionKeyWithMultipleProperties(
  key: Key)

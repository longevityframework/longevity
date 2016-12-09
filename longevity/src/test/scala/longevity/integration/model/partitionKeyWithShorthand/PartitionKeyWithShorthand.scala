package longevity.integration.model.partitionKeyWithShorthand

import longevity.model.annotations.persistent

@persistent(keySet = Set(partitionKey(props.key)))
case class PartitionKeyWithShorthand(
  key: Key)

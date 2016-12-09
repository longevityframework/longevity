package longevity.integration.model.partitionKey

import longevity.model.annotations.persistent

// NOTE unfortunate name clash here with longevity.model.ptype.PartitionKey

@persistent(keySet = Set(partitionKey(props.key)))
case class PartitionKey(key: Key)

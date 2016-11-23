package longevity.integration.subdomain.partitionKeyWithShorthand

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(partitionKey(props.key)))
case class PartitionKeyWithShorthand(
  key: Key)

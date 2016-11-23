package longevity.integration.subdomain.partitionKeyWithMultipleProperties

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.key)))
case class PartitionKeyWithMultipleProperties(
  key: Key)

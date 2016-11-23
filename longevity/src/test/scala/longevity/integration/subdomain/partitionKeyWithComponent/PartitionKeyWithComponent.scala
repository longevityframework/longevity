package longevity.integration.subdomain.partitionKeyWithComponent

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(partitionKey(props.key)))
case class PartitionKeyWithComponent(key: Key)

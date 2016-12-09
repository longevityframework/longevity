package longevity.integration.subdomain.partitionKeyWithForeignKey

import longevity.model.annotations.persistent

@persistent(keySet = Set(partitionKey(props.key)))
case class PartitionKeyWithForeignKey(key: Key)

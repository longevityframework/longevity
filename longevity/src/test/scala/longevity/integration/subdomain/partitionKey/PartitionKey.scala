package longevity.integration.subdomain.partitionKey

import longevity.subdomain.annotations.persistent

// NOTE unfortunate name clash here with longevity.subdomain.ptype.PartitionKey

@persistent(keySet = Set(partitionKey(props.key)))
case class PartitionKey(key: Key)

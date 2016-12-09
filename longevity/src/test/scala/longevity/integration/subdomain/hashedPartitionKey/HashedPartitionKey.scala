package longevity.integration.subdomain.hashedPartitionKey

import longevity.model.annotations.persistent

@persistent(keySet = Set(
  partitionKey(props.key, hashed = true)))
case class HashedPartitionKey(key: Key)

package longevity.integration.subdomain.partitionKeyWithSecondaryKey

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(
  partitionKey(props.primary),
  key(props.secondary)))
case class PartitionKeyWithSecondaryKey(
  primary: Key,
  secondary: SecondaryKey)

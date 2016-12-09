package longevity.integration.subdomain.partitionKeyWithSecondaryKey

import longevity.model.annotations.persistent

@persistent(keySet = Set(
  partitionKey(props.primary),
  key(props.secondary)))
case class PartitionKeyWithSecondaryKey(
  primary: Key,
  secondary: SecondaryKey)

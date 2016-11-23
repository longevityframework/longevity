package longevity.integration.subdomain.partitionKeyInComponent

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(partitionKey(props.component.key)))
case class PartitionKeyInComponent(
  filler: String,
  component: Component)

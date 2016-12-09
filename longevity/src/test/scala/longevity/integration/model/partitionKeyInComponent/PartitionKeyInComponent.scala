package longevity.integration.model.partitionKeyInComponent

import longevity.model.annotations.persistent

@persistent(keySet = Set(partitionKey(props.component.key)))
case class PartitionKeyInComponent(
  filler: String,
  component: Component)

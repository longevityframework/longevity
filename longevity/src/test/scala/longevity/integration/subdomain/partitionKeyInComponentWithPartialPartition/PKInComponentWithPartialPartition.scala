package longevity.integration.subdomain.partitionKeyInComponentWithPartialPartition

import longevity.model.annotations.persistent

@persistent(keySet = Set(partitionKey(props.component.key, partition(props.component.key.part1))))
case class PKInComponentWithPartialPartition(
  filler: String,
  component: Component)

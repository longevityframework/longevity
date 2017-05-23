package longevity.integration.model.primaryKeyInComponentWithPartialPartition

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(primaryKey(props.component.key, partition(props.component.key.part1))))
case class PKInComponentWithPartialPartition(
  filler: String,
  component: Component)

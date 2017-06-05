package longevity.integration.model.primaryKeyInComponentWithPartialPartition

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class PKInComponentWithPartialPartition(
  filler: String,
  component: Component)

object PKInComponentWithPartialPartition {
  implicit val idKey = primaryKey(props.component.key, partition(props.component.key.part1))
}

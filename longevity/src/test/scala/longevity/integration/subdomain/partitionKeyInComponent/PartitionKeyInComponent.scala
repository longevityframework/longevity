package longevity.integration.subdomain.partitionKeyInComponent

import longevity.subdomain.PType

case class PartitionKeyInComponent(
  filler: String,
  component: Component)

object PartitionKeyInComponent extends PType[PartitionKeyInComponent] {
  object props {
    val key = prop[Key]("component.key")
  }
  object keys {
    val primaryKey = partitionKey(props.key)
  }
}

package longevity.integration.subdomain.partitionKeyWithSecondaryKey

import longevity.subdomain.PType

case class PartitionKeyWithSecondaryKey(
  primary: Key,
  secondary: SecondaryKey)

object PartitionKeyWithSecondaryKey extends PType[PartitionKeyWithSecondaryKey] {
  object props {
    val primary = prop[Key]("primary")
    val secondary = prop[SecondaryKey]("secondary")
  }
  object keys {
    val primary = partitionKey(props.primary)
    val secondary = key(props.secondary)
  }
}

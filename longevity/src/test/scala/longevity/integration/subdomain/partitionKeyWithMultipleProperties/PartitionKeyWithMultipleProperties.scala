package longevity.integration.subdomain.partitionKeyWithMultipleProperties

import longevity.subdomain.PType

case class PartitionKeyWithMultipleProperties(
  key: Key)

object PartitionKeyWithMultipleProperties extends PType[PartitionKeyWithMultipleProperties] {
  object props {
    val key = prop[Key]("key")
  }
  object keys {
    val key = partitionKey(props.key)
  }
}

package longevity.integration.subdomain.partitionKeyWithForeignKey

import longevity.subdomain.PType

case class PartitionKeyWithForeignKey(key: Key)

object PartitionKeyWithForeignKey extends PType[PartitionKeyWithForeignKey] {
  object props {
    val key = prop[Key]("key")
  }
  object keys {
    val key = partitionKey(props.key)
  }
}

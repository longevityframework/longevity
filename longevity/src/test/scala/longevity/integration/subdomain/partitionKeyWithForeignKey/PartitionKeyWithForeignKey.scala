package longevity.integration.subdomain.partitionKeyWithForeignKey

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class PartitionKeyWithForeignKey(
  key: Key)
extends Persistent

object PartitionKeyWithForeignKey extends PType[PartitionKeyWithForeignKey] {
  object props {
    val key = prop[Key]("key")
  }
  object keys {
    val key = partitionKey(props.key)
  }
}

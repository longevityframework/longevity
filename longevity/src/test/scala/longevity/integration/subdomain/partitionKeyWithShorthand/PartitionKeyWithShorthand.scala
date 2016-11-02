package longevity.integration.subdomain.partitionKeyWithShorthand

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class PartitionKeyWithShorthand(
  key: Key)
extends Persistent

object PartitionKeyWithShorthand extends PType[PartitionKeyWithShorthand] {
  object props {
    val key = prop[Key]("key")
  }
  object keys {
    val key = partitionKey(props.key)
  }
}

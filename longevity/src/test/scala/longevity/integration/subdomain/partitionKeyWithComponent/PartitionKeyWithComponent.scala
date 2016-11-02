package longevity.integration.subdomain.partitionKeyWithComponent

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class PartitionKeyWithComponent(
  key: Key)
extends Persistent

object PartitionKeyWithComponent extends PType[PartitionKeyWithComponent] {
  object props {
    val key = prop[Key]("key")
  }
  object keys {
    val key = partitionKey(props.key)
  }
}

package longevity.integration.subdomain.partitionKeyWithComplexPartialPartition

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class PartitionKeyWithComplexPartialPartition(
  key: Key)
extends Persistent

object PartitionKeyWithComplexPartialPartition extends PType[PartitionKeyWithComplexPartialPartition] {
  object props {
    val key = prop[Key]("key")
    val keyProp1 = prop[String]("key.prop1")
    val subKeyProp1 = prop[String]("key.subKey.prop1")
  }
  object keys {
    val key = partitionKey(props.key, partition(props.keyProp1, props.subKeyProp1))
  }
}

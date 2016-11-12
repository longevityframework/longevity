package longevity.integration.subdomain.partitionKeyWithPartialPartition

import longevity.subdomain.PType

case class PartitionKeyWithPartialPartition(
  key: Key)

object PartitionKeyWithPartialPartition extends PType[PartitionKeyWithPartialPartition] {
  object props {
    val key = prop[Key]("key")
    val keyProp1 = prop[String]("key.prop1")
  }
  object keys {
    val key = partitionKey(props.key, partition(props.keyProp1))
  }
}

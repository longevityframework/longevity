package longevity.integration.subdomain.partitionKeyWithComplexPartialPartition

import longevity.subdomain.PType

case class PartitionKeyWithComplexPartialPartition(
  key: Key)

object PartitionKeyWithComplexPartialPartition extends PType[PartitionKeyWithComplexPartialPartition] {
  object props {
    val key = prop[Key]("key")
    val keyProp1 = prop[String]("key.prop1")
    val subKeyProp1 = prop[String]("key.subKey.prop1")
    val subKeyProp2 = prop[String]("key.subKey.prop2")
    val keyProp2 = prop[String]("key.prop2")
  }
  object keys {
    val key = partitionKey(props.key, partition(props.keyProp1, props.subKeyProp1))
  }
}

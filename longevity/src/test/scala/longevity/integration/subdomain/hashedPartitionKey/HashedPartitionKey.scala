package longevity.integration.subdomain.hashedPartitionKey

import longevity.subdomain.PType

case class HashedPartitionKey(key: Key)

object HashedPartitionKey extends PType[HashedPartitionKey] {
  object props {
    val key = prop[Key]("key")
  }
  object keys {
    val key = partitionKey(props.key, hashed = true)
  }
}

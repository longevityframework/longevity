package longevity.integration.subdomain.hashedPartitionKey

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class HashedPartitionKey(key: Key) extends Persistent

object HashedPartitionKey extends PType[HashedPartitionKey] {
  object props {
    val key = prop[Key]("key")
  }
  object keys {
    val key = partitionKey(props.key, hashed = true)
  }
}

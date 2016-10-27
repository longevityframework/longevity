package longevity.integration.subdomain.partitionKey

import longevity.subdomain.Persistent
import longevity.subdomain.PType

// NOTE unfortunate name clash here with longevity.subdomain.ptype.PartitionKey
case class PartitionKey(id: PartitionKeyId)
extends Persistent

object PartitionKey extends PType[PartitionKey] {
  object props {
    val id = prop[PartitionKeyId]("id")
  }
  object keys {
    val id = partitionKey(props.id)
  }
}

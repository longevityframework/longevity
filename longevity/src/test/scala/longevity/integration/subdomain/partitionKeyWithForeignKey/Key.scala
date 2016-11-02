package longevity.integration.subdomain.partitionKeyWithForeignKey

import longevity.subdomain.KeyVal

case class Key(
  id: String,
  associated: AssociatedId)
extends KeyVal[PartitionKeyWithForeignKey, Key]

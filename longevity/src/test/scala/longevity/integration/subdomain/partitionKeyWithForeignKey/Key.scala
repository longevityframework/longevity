package longevity.integration.subdomain.partitionKeyWithForeignKey

import longevity.subdomain.annotations.keyVal

@keyVal[PartitionKeyWithForeignKey]
case class Key(
  id: String,
  associated: AssociatedId)

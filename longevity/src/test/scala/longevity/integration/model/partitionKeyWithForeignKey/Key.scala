package longevity.integration.model.partitionKeyWithForeignKey

import longevity.model.annotations.keyVal

@keyVal[PartitionKeyWithForeignKey]
case class Key(
  id: String,
  associated: AssociatedId)

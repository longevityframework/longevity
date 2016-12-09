package longevity.integration.model.partitionKey

import longevity.model.annotations.keyVal

@keyVal[PartitionKey]
case class Key(id: String)

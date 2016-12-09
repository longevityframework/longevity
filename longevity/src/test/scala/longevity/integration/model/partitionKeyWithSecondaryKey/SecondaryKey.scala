package longevity.integration.model.partitionKeyWithSecondaryKey

import longevity.model.annotations.keyVal

@keyVal[PartitionKeyWithSecondaryKey]
case class SecondaryKey(id: String)

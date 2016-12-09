package longevity.integration.subdomain.partitionKeyWithSecondaryKey

import longevity.model.annotations.keyVal

@keyVal[PartitionKeyWithSecondaryKey]
case class Key(id: String)

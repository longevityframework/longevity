package longevity.integration.subdomain.partitionKeyWithSecondaryKey

import longevity.subdomain.annotations.keyVal

@keyVal[PartitionKeyWithSecondaryKey]
case class SecondaryKey(id: String)

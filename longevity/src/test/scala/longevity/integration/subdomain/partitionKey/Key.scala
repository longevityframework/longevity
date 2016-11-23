package longevity.integration.subdomain.partitionKey

import longevity.subdomain.annotations.keyVal

@keyVal[PartitionKey]
case class Key(id: String)

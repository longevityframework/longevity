package longevity.integration.subdomain.partitionKey

import longevity.model.annotations.keyVal

@keyVal[PartitionKey]
case class Key(id: String)

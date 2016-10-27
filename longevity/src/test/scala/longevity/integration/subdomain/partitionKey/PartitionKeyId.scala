package longevity.integration.subdomain.partitionKey

import longevity.subdomain.KeyVal

case class PartitionKeyId(id: String) extends KeyVal[PartitionKey, PartitionKeyId]

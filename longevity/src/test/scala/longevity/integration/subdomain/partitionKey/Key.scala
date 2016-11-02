package longevity.integration.subdomain.partitionKey

import longevity.subdomain.KeyVal

case class Key(id: String) extends KeyVal[PartitionKey, Key]

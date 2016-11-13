package longevity.integration.subdomain.partitionKeyWithSecondaryKey

import longevity.subdomain.KeyVal

case class SecondaryKey(id: String) extends KeyVal[PartitionKeyWithSecondaryKey]

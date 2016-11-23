package longevity.integration.subdomain.partitionKeyWithMultipleProperties

import longevity.subdomain.annotations.keyVal

@keyVal[PartitionKeyWithMultipleProperties]
case class Key(prop1: String, prop2: String)

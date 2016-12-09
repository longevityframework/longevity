package longevity.integration.subdomain.partitionKeyWithMultipleProperties

import longevity.model.annotations.keyVal

@keyVal[PartitionKeyWithMultipleProperties]
case class Key(prop1: String, prop2: String)

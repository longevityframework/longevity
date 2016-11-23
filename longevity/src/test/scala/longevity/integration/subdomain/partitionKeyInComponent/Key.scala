package longevity.integration.subdomain.partitionKeyInComponent

import longevity.subdomain.annotations.keyVal

@keyVal[PartitionKeyInComponent]
case class Key(id: String)

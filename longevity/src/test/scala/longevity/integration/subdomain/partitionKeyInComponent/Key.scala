package longevity.integration.subdomain.partitionKeyInComponent

import longevity.model.annotations.keyVal

@keyVal[PartitionKeyInComponent]
case class Key(id: String)

package longevity.integration.subdomain.partitionKeyWithComponent

import longevity.subdomain.annotations.keyVal

@keyVal[PartitionKeyWithComponent]
case class Key(id: String, component: Component)

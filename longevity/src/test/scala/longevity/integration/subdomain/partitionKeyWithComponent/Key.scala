package longevity.integration.subdomain.partitionKeyWithComponent

import longevity.model.annotations.keyVal

@keyVal[PartitionKeyWithComponent]
case class Key(id: String, component: Component)

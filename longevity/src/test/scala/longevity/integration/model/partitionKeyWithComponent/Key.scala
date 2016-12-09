package longevity.integration.model.partitionKeyWithComponent

import longevity.model.annotations.keyVal

@keyVal[PartitionKeyWithComponent]
case class Key(id: String, component: Component)

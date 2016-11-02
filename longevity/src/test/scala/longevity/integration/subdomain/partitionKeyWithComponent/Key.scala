package longevity.integration.subdomain.partitionKeyWithComponent

import longevity.subdomain.KeyVal

case class Key(
  id: String,
  component: Component)
extends KeyVal[PartitionKeyWithComponent, Key]

package longevity.integration.subdomain.partitionKeyWithShorthand

import longevity.subdomain.KeyVal

case class Key(
  id: String,
  uri: Uri)
extends KeyVal[PartitionKeyWithShorthand, Key]

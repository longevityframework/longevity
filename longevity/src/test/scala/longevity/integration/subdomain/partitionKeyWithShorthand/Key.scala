package longevity.integration.subdomain.partitionKeyWithShorthand

import longevity.subdomain.annotations.keyVal

@keyVal[PartitionKeyWithShorthand]
case class Key(id: String, uri: Uri)

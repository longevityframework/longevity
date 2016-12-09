package longevity.integration.subdomain.partitionKeyWithShorthand

import longevity.model.annotations.keyVal

@keyVal[PartitionKeyWithShorthand]
case class Key(id: String, uri: Uri)

package longevity.integration.model.hashedPartitionKey

import longevity.model.annotations.keyVal

@keyVal[HashedPartitionKey]
case class Key(id: String)

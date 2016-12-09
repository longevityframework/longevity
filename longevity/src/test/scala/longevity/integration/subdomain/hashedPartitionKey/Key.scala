package longevity.integration.subdomain.hashedPartitionKey

import longevity.model.annotations.keyVal

@keyVal[HashedPartitionKey]
case class Key(id: String)

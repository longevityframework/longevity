package longevity.integration.subdomain.hashedPartitionKey

import longevity.subdomain.annotations.keyVal

@keyVal[HashedPartitionKey]
case class Key(id: String)

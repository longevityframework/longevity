package longevity.integration.subdomain.keyWithForeignKey

import longevity.subdomain.annotations.keyVal

@keyVal[KeyWithForeignKey]
case class SecondaryKey(id: String, associated: AssociatedId)

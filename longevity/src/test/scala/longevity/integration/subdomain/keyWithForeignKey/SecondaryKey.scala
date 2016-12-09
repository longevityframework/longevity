package longevity.integration.subdomain.keyWithForeignKey

import longevity.model.annotations.keyVal

@keyVal[KeyWithForeignKey]
case class SecondaryKey(id: String, associated: AssociatedId)

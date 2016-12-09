package longevity.integration.model.keyWithForeignKey

import longevity.model.annotations.keyVal

@keyVal[KeyWithForeignKey]
case class SecondaryKey(id: String, associated: AssociatedId)

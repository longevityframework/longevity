package longevity.integration.model.keyWithForeignKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, KeyWithForeignKey]
case class SecondaryKey(id: String, associated: AssociatedId)

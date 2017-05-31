package longevity.integration.model.keyWithForeignKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, Associated]
case class AssociatedId(id: String)

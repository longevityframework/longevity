package longevity.integration.model.componentWithForeignKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, Associated]
case class AssociatedId(id: String)

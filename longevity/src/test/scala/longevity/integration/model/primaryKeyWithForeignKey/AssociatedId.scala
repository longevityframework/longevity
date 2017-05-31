package longevity.integration.model.primaryKeyWithForeignKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, Associated]
case class AssociatedId(id: String)

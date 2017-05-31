package longevity.integration.model.foreignKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, Associated]
case class AssociatedId(id: String)

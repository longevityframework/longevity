package longevity.integration.model.primaryKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, PrimaryKey]
case class Key(id: String)

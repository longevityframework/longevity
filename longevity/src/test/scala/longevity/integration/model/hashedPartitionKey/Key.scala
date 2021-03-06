package longevity.integration.model.hashedPrimaryKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, HashedPrimaryKey]
case class Key(id: String)

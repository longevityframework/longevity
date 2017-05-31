package longevity.integration.model.primaryKeyWithSecondaryKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, PrimaryKeyWithSecondaryKey]
case class Key(id: String)

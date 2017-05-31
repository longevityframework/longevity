package longevity.integration.model.keyWithForeignKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, KeyWithForeignKey]
case class KeyWithForeignKeyId(id: String)

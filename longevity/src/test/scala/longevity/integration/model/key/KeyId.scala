package longevity.integration.model.key

import longevity.model.annotations.keyVal

@keyVal[DomainModel, Key]
case class KeyId(id: String)

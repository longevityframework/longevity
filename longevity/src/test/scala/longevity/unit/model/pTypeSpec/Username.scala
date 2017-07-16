package longevity.unit.model.pTypeSpec

import longevity.model.annotations.keyVal

@keyVal[DomainModel, User]
case class Username(username: String)

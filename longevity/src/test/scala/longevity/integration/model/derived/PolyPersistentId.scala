package longevity.integration.model.derived

import longevity.model.annotations.keyVal

@keyVal[DomainModel, PolyPersistent]
case class PolyPersistentId(id: String)

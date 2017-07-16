package longevity.integration.model.intKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, IntKey]
case class IntKeyId(id: Int)

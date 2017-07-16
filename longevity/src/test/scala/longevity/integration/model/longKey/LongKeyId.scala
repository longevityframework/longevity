package longevity.integration.model.longKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, LongKey]
case class LongKeyId(id: Int)

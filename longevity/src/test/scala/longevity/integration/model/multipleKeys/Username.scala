package longevity.integration.model.multipleKeys

import longevity.model.annotations.keyVal

@keyVal[DomainModel, MultipleKeys]
case class Username(username: String)

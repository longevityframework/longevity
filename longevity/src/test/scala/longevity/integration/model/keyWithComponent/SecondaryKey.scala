package longevity.integration.model.keyWithComponent

import longevity.model.annotations.keyVal

@keyVal[DomainModel, KeyWithComponent]
case class SecondaryKey(id: String, component: Component)

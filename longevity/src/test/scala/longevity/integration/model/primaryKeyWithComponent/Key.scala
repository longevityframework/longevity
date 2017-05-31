package longevity.integration.model.primaryKeyWithComponent

import longevity.model.annotations.keyVal

@keyVal[DomainModel, PrimaryKeyWithComponent]
case class Key(id: String, component: Component)

package longevity.integration.model.primaryKeyInComponent

import longevity.model.annotations.keyVal

@keyVal[DomainModel, PrimaryKeyInComponent]
case class Key(id: String)

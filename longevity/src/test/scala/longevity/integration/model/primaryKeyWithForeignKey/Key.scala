package longevity.integration.model.primaryKeyWithForeignKey

import longevity.model.annotations.keyVal

@keyVal[DomainModel, PrimaryKeyWithForeignKey]
case class Key(
  id: String,
  associated: AssociatedId)

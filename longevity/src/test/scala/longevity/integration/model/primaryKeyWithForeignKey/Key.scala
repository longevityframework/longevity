package longevity.integration.model.primaryKeyWithForeignKey

import longevity.model.annotations.keyVal

@keyVal[PrimaryKeyWithForeignKey]
case class Key(
  id: String,
  associated: AssociatedId)

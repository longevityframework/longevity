package longevity.integration.model.primaryKeyWithShorthand

import longevity.model.annotations.keyVal

@keyVal[DomainModel, PrimaryKeyWithShorthand]
case class Key(id: String, uri: Uri)

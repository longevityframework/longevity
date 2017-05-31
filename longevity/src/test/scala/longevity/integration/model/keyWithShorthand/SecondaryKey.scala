package longevity.integration.model.keyWithShorthand

import longevity.model.annotations.keyVal

@keyVal[DomainModel, KeyWithShorthand]
case class SecondaryKey(id: String, uri: Uri)

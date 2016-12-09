package longevity.integration.model.keyWithShorthand

import longevity.model.annotations.keyVal

@keyVal[KeyWithShorthand]
case class SecondaryKey(id: String, uri: Uri)

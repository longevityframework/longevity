package longevity.integration.subdomain.keyWithShorthand

import longevity.subdomain.annotations.keyVal

@keyVal[KeyWithShorthand]
case class SecondaryKey(id: String, uri: Uri)

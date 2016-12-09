package longevity.integration.subdomain.keyWithMultipleProperties

import longevity.model.annotations.keyVal

@keyVal[KeyWithMultipleProperties]
case class SecondaryKey(prop1: String, prop2: String)

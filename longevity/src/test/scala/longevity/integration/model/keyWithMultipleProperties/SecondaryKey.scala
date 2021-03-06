package longevity.integration.model.keyWithMultipleProperties

import longevity.model.annotations.keyVal

@keyVal[DomainModel, KeyWithMultipleProperties]
case class SecondaryKey(prop1: String, prop2: String)

package longevity.integration.model.primaryKeyWithMultipleProperties

import longevity.model.annotations.keyVal

@keyVal[PrimaryKeyWithMultipleProperties]
case class Key(prop1: String, prop2: String)

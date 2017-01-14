package longevity.integration.model.primaryKeyWithMultipleProperties

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.key)))
case class PrimaryKeyWithMultipleProperties(
  key: Key)

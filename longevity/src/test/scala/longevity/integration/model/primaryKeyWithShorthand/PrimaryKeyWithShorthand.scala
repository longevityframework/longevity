package longevity.integration.model.primaryKeyWithShorthand

import longevity.model.annotations.persistent

@persistent(keySet = Set(primaryKey(props.key)))
case class PrimaryKeyWithShorthand(
  key: Key)

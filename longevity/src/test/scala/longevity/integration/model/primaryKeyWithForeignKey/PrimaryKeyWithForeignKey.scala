package longevity.integration.model.primaryKeyWithForeignKey

import longevity.model.annotations.persistent

@persistent(keySet = Set(primaryKey(props.key)))
case class PrimaryKeyWithForeignKey(key: Key)

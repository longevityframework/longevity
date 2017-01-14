package longevity.integration.model.hashedPrimaryKey

import longevity.model.annotations.persistent

@persistent(keySet = Set(
  primaryKey(props.key, hashed = true)))
case class HashedPrimaryKey(key: Key)

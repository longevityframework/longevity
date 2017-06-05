package longevity.integration.model.hashedPrimaryKey

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class HashedPrimaryKey(key: Key)

object HashedPrimaryKey {
  implicit lazy val hashedKey = primaryKey(props.key, hashed = true)
}

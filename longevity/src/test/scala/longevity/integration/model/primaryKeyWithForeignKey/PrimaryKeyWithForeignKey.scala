package longevity.integration.model.primaryKeyWithForeignKey

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class PrimaryKeyWithForeignKey(key: Key)

object PrimaryKeyWithForeignKey {
  implicit lazy val keyKey = key(props.key)
}

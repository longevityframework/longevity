package longevity.integration.model.primaryKeyWithMultipleProperties

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class PrimaryKeyWithMultipleProperties(key: Key)

object PrimaryKeyWithMultipleProperties {
  implicit val keyKey = key(props.key)
}

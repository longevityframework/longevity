package longevity.integration.model.primaryKeyWithShorthand

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class PrimaryKeyWithShorthand(key: Key)

object PrimaryKeyWithShorthand {
  implicit val keyKey = key(props.key)
}

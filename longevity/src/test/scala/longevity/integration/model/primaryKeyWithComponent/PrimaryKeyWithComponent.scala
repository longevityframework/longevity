package longevity.integration.model.primaryKeyWithComponent

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class PrimaryKeyWithComponent(key: Key)

object PrimaryKeyWithComponent {
  implicit lazy val keyKey = key(props.key)
}

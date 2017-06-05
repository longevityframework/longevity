package longevity.integration.model.multipleKeys

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class MultipleKeys(
  id: MultipleKeysId,
  username: Username)

object MultipleKeys {
  implicit lazy val idKey = key(props.id)
  implicit lazy val usernameKey = key(props.username)
}

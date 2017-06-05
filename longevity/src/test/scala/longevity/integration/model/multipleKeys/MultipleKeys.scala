package longevity.integration.model.multipleKeys

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class MultipleKeys(
  id: MultipleKeysId,
  username: Username)

object MultipleKeys {
  implicit val idKey = key(props.id)
  implicit val usernameKey = key(props.username)
}

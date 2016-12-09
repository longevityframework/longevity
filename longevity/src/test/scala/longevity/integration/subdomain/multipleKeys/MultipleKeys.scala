package longevity.integration.subdomain.multipleKeys

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.id), key(props.username)))
case class MultipleKeys(
  id: MultipleKeysId,
  username: Username)

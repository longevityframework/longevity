package longevity.integration.model.keyWithForeignKey

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(key(props.id), key(props.secondaryKey)))
case class KeyWithForeignKey(
  id: KeyWithForeignKeyId,
  secondaryKey: SecondaryKey)

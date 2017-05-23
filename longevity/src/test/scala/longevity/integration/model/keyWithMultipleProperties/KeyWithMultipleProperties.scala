package longevity.integration.model.keyWithMultipleProperties

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(key(props.id), key(props.secondaryKey)))
case class KeyWithMultipleProperties(
  id: KeyWithMultiplePropertiesId,
  secondaryKey: SecondaryKey)

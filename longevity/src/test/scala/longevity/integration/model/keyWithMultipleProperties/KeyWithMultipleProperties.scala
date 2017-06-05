package longevity.integration.model.keyWithMultipleProperties

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class KeyWithMultipleProperties(
  id: KeyWithMultiplePropertiesId,
  secondaryKey: SecondaryKey)

object KeyWithMultipleProperties {
  implicit lazy val idKey = key(props.id)
  implicit lazy val secondaryKey = key(props.secondaryKey)
}

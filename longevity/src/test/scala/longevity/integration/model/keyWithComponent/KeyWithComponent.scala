package longevity.integration.model.keyWithComponent

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class KeyWithComponent(
  id: KeyWithComponentId,
  secondaryKey: SecondaryKey)

object KeyWithComponent {
  implicit lazy val idKey = key(props.id)
  implicit lazy val secondaryKey = key(props.secondaryKey)
}

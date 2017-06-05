package longevity.integration.model.keyWithComponent

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class KeyWithComponent(
  id: KeyWithComponentId,
  secondaryKey: SecondaryKey)

object KeyWithComponent {
  implicit val idKey = key(props.id)
  implicit val secondaryKey = key(props.secondaryKey)
}

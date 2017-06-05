package longevity.integration.model.keyWithShorthand

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class KeyWithShorthand(
  id: KeyWithShorthandId,
  secondaryKey: SecondaryKey)

object KeyWithShorthand {
  implicit val idKey = key(props.id)
  implicit val secondaryKey = key(props.secondaryKey)
}

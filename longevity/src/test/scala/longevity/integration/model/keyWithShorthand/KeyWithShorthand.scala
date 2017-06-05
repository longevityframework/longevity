package longevity.integration.model.keyWithShorthand

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class KeyWithShorthand(
  id: KeyWithShorthandId,
  secondaryKey: SecondaryKey)

object KeyWithShorthand {
  implicit lazy val idKey = key(props.id)
  implicit lazy val secondaryKey = key(props.secondaryKey)
}

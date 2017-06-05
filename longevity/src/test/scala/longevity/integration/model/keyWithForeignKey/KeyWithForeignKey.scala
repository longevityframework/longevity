package longevity.integration.model.keyWithForeignKey

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class KeyWithForeignKey(
  id: KeyWithForeignKeyId,
  secondaryKey: SecondaryKey)

object KeyWithForeignKey {
  implicit val idKey = key(props.id)
  implicit val secondaryKey = key(props.secondaryKey)
}

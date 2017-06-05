package longevity.integration.model.primaryKeyWithSecondaryKey

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class PrimaryKeyWithSecondaryKey(
  primary: Key,
  secondary: SecondaryKey)

object PrimaryKeyWithSecondaryKey {
  implicit val idKey = primaryKey(props.primary)
  implicit val secondaryKey = key(props.secondary)
}

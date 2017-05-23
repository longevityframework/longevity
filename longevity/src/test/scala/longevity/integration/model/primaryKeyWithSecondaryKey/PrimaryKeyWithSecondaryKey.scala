package longevity.integration.model.primaryKeyWithSecondaryKey

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(
  primaryKey(props.primary),
  key(props.secondary)))
case class PrimaryKeyWithSecondaryKey(
  primary: Key,
  secondary: SecondaryKey)

package longevity.integration.model.primaryKeyWithShorthand

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(primaryKey(props.key)))
case class PrimaryKeyWithShorthand(
  key: Key)

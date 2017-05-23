package longevity.integration.model.primaryKeyWithComponent

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(primaryKey(props.key)))
case class PrimaryKeyWithComponent(key: Key)

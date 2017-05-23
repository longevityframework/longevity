package longevity.integration.model.primaryKeyWithForeignKey

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(primaryKey(props.key)))
case class PrimaryKeyWithForeignKey(key: Key)

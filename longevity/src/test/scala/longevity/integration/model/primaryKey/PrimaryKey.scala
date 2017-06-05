package longevity.integration.model.primaryKey

import longevity.model.annotations.persistent

// NOTE unfortunate name clash here with longevity.model.ptype.PrimaryKey

@persistent[DomainModel]
case class PrimaryKey(key: Key)

object PrimaryKey {
  implicit val keyKey = primaryKey(props.key)
}

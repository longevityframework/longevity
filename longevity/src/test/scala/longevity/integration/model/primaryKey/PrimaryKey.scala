package longevity.integration.model.primaryKey

import longevity.model.annotations.persistent

// NOTE unfortunate name clash here with longevity.model.ptype.PrimaryKey

@persistent(keySet = Set(primaryKey(props.key)))
case class PrimaryKey(key: Key)

package longevity.integration.model.primaryKeyInComponent

import longevity.model.annotations.persistent

@persistent(keySet = Set(primaryKey(props.component.key)))
case class PrimaryKeyInComponent(
  filler: String,
  component: Component)

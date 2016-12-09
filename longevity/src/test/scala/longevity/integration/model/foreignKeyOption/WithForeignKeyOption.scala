package longevity.integration.model.foreignKeyOption

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class WithForeignKeyOption(
  id: WithForeignKeyOptionId,
  associated: Option[AssociatedId])

package longevity.integration.model.foreignKeySet

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(key(props.id)))
case class WithForeignKeySet(
  id: WithForeignKeySetId,
  associated: Set[AssociatedId])

package longevity.integration.subdomain.foreignKeySet

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class WithForeignKeySet(
  id: WithForeignKeySetId,
  associated: Set[AssociatedId])

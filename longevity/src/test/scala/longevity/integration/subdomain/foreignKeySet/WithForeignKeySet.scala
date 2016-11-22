package longevity.integration.subdomain.foreignKeySet

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class WithForeignKeySet(
  id: WithForeignKeySetId,
  associated: Set[AssociatedId])

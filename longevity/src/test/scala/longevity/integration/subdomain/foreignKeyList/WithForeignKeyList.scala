package longevity.integration.subdomain.foreignKeyList

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class WithForeignKeyList(
  id: WithForeignKeyListId,
  associated: List[AssociatedId])

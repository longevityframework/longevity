package longevity.integration.model.foreignKeyList

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithForeignKeyList(
  id: WithForeignKeyListId,
  associated: List[AssociatedId])

object WithForeignKeyList {
  implicit lazy val idKey = key(props.id)
}

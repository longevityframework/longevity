package longevity.integration.model.foreignKeySet

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithForeignKeySet(
  id: WithForeignKeySetId,
  associated: Set[AssociatedId])

object WithForeignKeySet {
  implicit lazy val idKey = key(props.id)
}

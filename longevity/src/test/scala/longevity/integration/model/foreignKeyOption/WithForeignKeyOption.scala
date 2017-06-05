package longevity.integration.model.foreignKeyOption

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithForeignKeyOption(
  id: WithForeignKeyOptionId,
  associated: Option[AssociatedId])

object WithForeignKeyOption {
  implicit lazy val idKey = key(props.id)
}

package longevity.integration.model.foreignKey

import longevity.model.annotations.persistent

@persistent[DomainModel](
  keySet = Set(key(props.id)),
  indexSet = Set(index(props.associated)))
case class WithForeignKey(
  id: WithForeignKeyId,
  associated: AssociatedId)

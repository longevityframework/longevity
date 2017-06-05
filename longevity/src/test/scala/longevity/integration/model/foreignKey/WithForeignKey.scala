package longevity.integration.model.foreignKey

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithForeignKey(
  id: WithForeignKeyId,
  associated: AssociatedId)

object WithForeignKey {
  implicit val idKey = key(props.id)
  override val indexSet = Set(index(props.associated))
}

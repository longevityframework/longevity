package longevity.integration.model.foreignKey

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithForeignKey(
  id: WithForeignKeyId,
  associated: AssociatedId)

object WithForeignKey {
  implicit lazy val idKey = key(props.id)
  override lazy val indexSet = Set(index(props.associated))
}

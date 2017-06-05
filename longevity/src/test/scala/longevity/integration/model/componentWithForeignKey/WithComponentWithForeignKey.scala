package longevity.integration.model.componentWithForeignKey

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithComponentWithForeignKey(
  id: WithComponentWithForeignKeyId,
  component: ComponentWithForeignKey)

object WithComponentWithForeignKey {
  implicit val idKey = key(props.id)
}

package longevity.integration.model.componentWithList

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithComponentWithList(
  id: WithComponentWithListId,
  component: Component)

object WithComponentWithList {
  implicit val idKey = key(props.id)
}

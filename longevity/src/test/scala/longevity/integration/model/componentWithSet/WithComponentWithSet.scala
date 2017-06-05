package longevity.integration.model.componentWithSet

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithComponentWithSet(
  id: WithComponentWithSetId,
  component: Component)

object WithComponentWithSet {
  implicit lazy val idKey = key(props.id)
}

package longevity.integration.model.componentWithOption

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithComponentWithOption(
  id: WithComponentWithOptionId,
  component: Component)

object WithComponentWithOption {
  implicit val idKey = key(props.id)
}

package longevity.integration.model.componentWithOption

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(key(WithComponentWithOption.props.id)))
case class WithComponentWithOption(
  id: WithComponentWithOptionId,
  component: Component)

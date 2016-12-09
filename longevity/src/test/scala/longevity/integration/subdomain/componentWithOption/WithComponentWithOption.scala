package longevity.integration.subdomain.componentWithOption

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(WithComponentWithOption.props.id)))
case class WithComponentWithOption(
  id: WithComponentWithOptionId,
  component: Component)

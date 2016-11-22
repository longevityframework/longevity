package longevity.integration.subdomain.componentWithOption

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(WithComponentWithOption.props.id)))
case class WithComponentWithOption(
  id: WithComponentWithOptionId,
  component: Component)

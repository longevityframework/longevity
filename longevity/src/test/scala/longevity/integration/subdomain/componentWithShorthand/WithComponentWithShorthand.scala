package longevity.integration.subdomain.componentShorthands

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class WithComponentWithShorthand(
  id: WithComponentWithShorthandId,
  component: ComponentWithShorthand)

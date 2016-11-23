package longevity.integration.subdomain.shorthandWithComponent

import longevity.subdomain.annotations.persistent

@persistent(
  keySet = Set(key(props.id)),
  indexSet = Set(index(props.shorthandWithComponent)))
case class WithShorthandWithComponent(
  id: WithShorthandWithComponentId,
  shorthandWithComponent: ShorthandWithComponent)

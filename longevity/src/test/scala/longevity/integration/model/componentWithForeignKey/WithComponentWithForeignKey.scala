package longevity.integration.model.componentWithForeignKey

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class WithComponentWithForeignKey(
  id: WithComponentWithForeignKeyId,
  component: ComponentWithForeignKey)

package longevity.integration.model.componentWithForeignKey

import longevity.model.annotations.component

@component
case class ComponentWithForeignKey(
  id: String,
  associatedId: AssociatedId)

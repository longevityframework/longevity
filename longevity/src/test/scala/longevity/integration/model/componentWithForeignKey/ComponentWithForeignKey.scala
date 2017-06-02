package longevity.integration.model.componentWithForeignKey

import longevity.model.annotations.component

@component[DomainModel]
case class ComponentWithForeignKey(
  id: String,
  associatedId: AssociatedId)

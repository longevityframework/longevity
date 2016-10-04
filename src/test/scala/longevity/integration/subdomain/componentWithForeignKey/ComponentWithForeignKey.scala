package longevity.integration.subdomain.componentWithForeignKey

import longevity.subdomain.embeddable.Embeddable

case class ComponentWithForeignKey(
  id: String,
  associatedId: AssociatedId)
extends Embeddable

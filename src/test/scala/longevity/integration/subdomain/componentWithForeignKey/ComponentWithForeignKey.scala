package longevity.integration.subdomain.componentWithForeignKey

import longevity.subdomain.Embeddable

case class ComponentWithForeignKey(
  id: String,
  associatedId: AssociatedId)
extends Embeddable

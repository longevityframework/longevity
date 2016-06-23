package longevity.integration.subdomain.withComponentWithSinglePropComponent

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class ComponentWithSinglePropComponent(uri: Uri) extends Entity

object ComponentWithSinglePropComponent extends EntityType[ComponentWithSinglePropComponent]

package longevity.integration.subdomain.shorthandWithComponent

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class ShorthandWithComponent(component: Component) extends ValueObject

object ShorthandWithComponent extends ValueType[ShorthandWithComponent]

package longevity.integration.subdomain.withMultiPropComponent

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class MultiPropComponent(
  prop1: String,
  prop2: String)
extends ValueObject

object MultiPropComponent extends ValueType[MultiPropComponent]

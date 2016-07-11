package longevity.integration.subdomain.keyWithComponent

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class Component(
  prop1: String,
  prop2: String)
extends ValueObject

object Component extends ValueType[Component]

package longevity.integration.subdomain.derived

import longevity.subdomain.annotations.derivedComponent

@derivedComponent[PolyComponent]
case class SecondDerivedComponent(
  id: PolyComponentId,
  second: String)
extends PolyComponent

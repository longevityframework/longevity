package longevity.integration.model.derived

import longevity.model.annotations.derivedComponent

@derivedComponent[PolyComponent]
case class SecondDerivedComponent(
  id: PolyComponentId,
  second: String)
extends PolyComponent
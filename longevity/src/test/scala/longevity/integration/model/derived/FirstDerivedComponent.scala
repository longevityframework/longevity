package longevity.integration.model.derived

import longevity.model.annotations.derivedComponent

@derivedComponent[PolyComponent]
case class FirstDerivedComponent(
  id: PolyComponentId,
  first: String)
extends PolyComponent

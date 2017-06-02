package longevity.integration.model.derived

import longevity.model.annotations.derivedComponent

@derivedComponent[DomainModel, PolyComponent]
case class SecondDerivedComponent(
  id: PolyComponentId,
  second: String)
extends PolyComponent

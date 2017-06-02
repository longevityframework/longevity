package longevity.integration.model.derived

import longevity.model.annotations.derivedComponent

@derivedComponent[DomainModel, PolyComponent]
case class FirstDerivedComponent(
  id: PolyComponentId,
  first: String)
extends PolyComponent

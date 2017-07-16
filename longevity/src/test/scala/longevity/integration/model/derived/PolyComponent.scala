package longevity.integration.model.derived

import longevity.model.annotations.derivedComponent
import longevity.model.annotations.polyComponent

@polyComponent[DomainModel]
sealed trait PolyComponent {
  val id: PolyComponentId
}

@derivedComponent[DomainModel, PolyComponent]
case class FirstDerivedComponent(
  id: PolyComponentId,
  first: String)
extends PolyComponent

@derivedComponent[DomainModel, PolyComponent]
case class SecondDerivedComponent(
  id: PolyComponentId,
  second: String)
extends PolyComponent

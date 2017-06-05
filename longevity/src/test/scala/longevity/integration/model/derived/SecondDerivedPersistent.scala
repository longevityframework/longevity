package longevity.integration.model.derived

import longevity.model.annotations.derivedPersistent

@derivedPersistent[DomainModel, PolyPersistent]
case class SecondDerivedPersistent(
  id: PolyPersistentId,
  second: String,
  component: PolyComponent)
extends PolyPersistent

object SecondDerivedPersistent {
  override val indexSet = Set(index(props.second))
}

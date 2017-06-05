package longevity.integration.model.derived

import longevity.model.annotations.derivedPersistent

@derivedPersistent[DomainModel, PolyPersistent]
case class FirstDerivedPersistent(
  id: PolyPersistentId,
  first: String,
  component: PolyComponent)
extends PolyPersistent

object FirstDerivedPersistent {
  implicit lazy val componentIdKey = key(props.component.id)
  override lazy val indexSet = Set(index(props.first))
}

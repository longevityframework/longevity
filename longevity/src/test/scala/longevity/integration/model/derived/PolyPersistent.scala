package longevity.integration.model.derived

import longevity.model.annotations.derivedPersistent
import longevity.model.annotations.polyPersistent

@polyPersistent[DomainModel]
sealed trait PolyPersistent {
  val id: PolyPersistentId
  val component: PolyComponent
}

object PolyPersistent {
  implicit val idKey = primaryKey(props.id)
}

@derivedPersistent[DomainModel, PolyPersistent]
case class FirstDerivedPersistent(
  id: PolyPersistentId,
  first: String,
  component: PolyComponent)
extends PolyPersistent

object FirstDerivedPersistent {
  implicit val componentIdKey = key(props.component.id)
  override val indexSet = Set(index(props.first))
}

@derivedPersistent[DomainModel, PolyPersistent]
case class SecondDerivedPersistent(
  id: PolyPersistentId,
  second: String,
  component: PolyComponent)
extends PolyPersistent

object SecondDerivedPersistent {
  override val indexSet = Set(index(props.second))
}

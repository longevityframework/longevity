package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.ptype.DerivedPType

case class FirstDerivedRoot(
  id: PolyRootId,
  first: String,
  component: PolyEntity)
extends PolyRoot

object FirstDerivedRoot extends DerivedPType[FirstDerivedRoot, PolyRoot] {
  object props {
    val first = prop[String]("first")
    val componentId = prop[PolyEntityId]("component.id")
  }
  object keys {
    val componentId = key(props.componentId)
  }
  object indexes {
    val first = index(props.first)
  }
}


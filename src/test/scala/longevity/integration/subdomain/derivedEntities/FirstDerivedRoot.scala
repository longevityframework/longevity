package longevity.integration.subdomain.derived

import longevity.subdomain.DerivedPType

case class FirstDerivedRoot(
  id: PolyRootId,
  first: String,
  component: PolyEmbeddable)
extends PolyRoot

object FirstDerivedRoot extends DerivedPType[FirstDerivedRoot, PolyRoot] {
  object props {
    val first = prop[String]("first")
    val componentId = prop[PolyEmbeddableId]("component.id")
  }
  object keys {
    val componentId = key(props.componentId)
  }
  object indexes {
    val first = index(props.first)
  }
}


package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.ptype.DerivedPType

case class FirstDerivedRoot(
  uri: String,
  first: String,
  component: PolyEntity)
extends PolyRoot

object FirstDerivedRoot extends DerivedPType[FirstDerivedRoot, PolyRoot] {
  val polyPType = PolyRoot
  object props {
    val first = prop[String]("first")
    val componentUri = prop[String]("component.uri")
  }
  object keys {
    val componentUri = key(props.componentUri)
  }
  object indexes {
    val first = index(props.first)
  }
}

package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.ptype.DerivedPType

case class SecondDerivedRoot(
  id: PolyRootId,
  second: String,
  component: PolyEntity)
extends PolyRoot

object SecondDerivedRoot extends DerivedPType[SecondDerivedRoot, PolyRoot] {
  val polyPType = PolyRoot
  object props {
    val second = prop[String]("second")
  }
  object keys {
  }
  object indexes {
    val second = index(props.second)
  }
}


package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.DerivedType
import longevity.subdomain.ptype.RootType

case class SecondDerivedRoot(
  uri: String,
  second: String,
  component: PolyEntity)
extends PolyRoot

object SecondDerivedRoot extends RootType[SecondDerivedRoot] with DerivedType[PolyRoot, SecondDerivedRoot] {
  val polyType = PolyRoot
  object props {
    val second = prop[String]("second")
  }
  object keys {
  }
  object indexes {
    val second = index(props.second)
  }
}


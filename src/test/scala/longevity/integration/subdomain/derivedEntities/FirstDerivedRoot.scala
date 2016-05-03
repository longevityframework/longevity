package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.DerivedPType
import longevity.subdomain.ptype.RootType

case class FirstDerivedRoot(
  uri: String,
  first: String,
  component: PolyEntity)
extends PolyRoot

object FirstDerivedRoot extends RootType[FirstDerivedRoot] with DerivedPType[PolyRoot, FirstDerivedRoot] {
  val polyPType = PolyRoot
  object props {
    val first = prop[String]("first")
  }
  object keys {
  }
  object indexes {
    val first = index(props.first)
  }
}


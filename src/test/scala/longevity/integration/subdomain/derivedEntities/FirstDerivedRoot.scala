package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.DerivedType
import longevity.subdomain.ptype.RootType

case class FirstDerivedRoot(
  uri: String,
  first: String,
  component: BaseEntity)
extends BaseRoot

object FirstDerivedRoot extends RootType[FirstDerivedRoot] with DerivedType[BaseRoot, FirstDerivedRoot] {
  val baseType = BaseRoot
  object props {
    val first = prop[String]("first")
  }
  object keys {
  }
  object indexes {
    val first = index(props.first)
  }
}


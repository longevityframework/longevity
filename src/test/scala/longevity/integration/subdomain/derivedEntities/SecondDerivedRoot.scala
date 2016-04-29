package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.DerivedType
import longevity.subdomain.ptype.RootType

case class SecondDerivedRoot(
  uri: String,
  second: String,
  component: BaseEntity)
extends BaseRoot

object SecondDerivedRoot extends RootType[SecondDerivedRoot] with DerivedType[BaseRoot, SecondDerivedRoot] {
  val polyType = BaseRoot
  object props {
    val second = prop[String]("second")
  }
  object keys {
  }
  object indexes {
    val second = index(props.second)
  }
}


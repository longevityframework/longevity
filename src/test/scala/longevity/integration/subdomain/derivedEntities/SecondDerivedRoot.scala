package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.DerivedType
import longevity.subdomain.ptype.RootType

case class SecondDerivedRoot(
  uri: String,
  second: String,
  component: SecondDerivedEntity)
extends BaseRoot

object SecondDerivedRoot extends RootType[SecondDerivedRoot] with DerivedType[BaseRoot, SecondDerivedRoot] {
  val baseType = BaseRoot
  object props {
    val second = prop[String]("second")
  }
  object keys {
  }
  object indexes {
    val second = index(props.second)
  }
}


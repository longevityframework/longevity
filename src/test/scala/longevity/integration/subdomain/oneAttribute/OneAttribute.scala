package longevity.integration.subdomain.oneAttribute

import longevity.subdomain._

case class OneAttribute(uri: String) extends Root

object OneAttribute extends RootType[OneAttribute] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}

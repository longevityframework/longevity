package longevity.integration.subdomain.withAssocSet

import longevity.subdomain._

case class Associated(uri: String) extends Root

object Associated extends RootType[Associated] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}

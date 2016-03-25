package longevity.integration.subdomain.keyWithShorthand

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType
import shorthands._

case class KeyWithShorthand(id: String, uri: Uri) extends Root

object KeyWithShorthand extends RootType[KeyWithShorthand] {
  object props {
    val id = prop[String]("id")
    val uri = prop[Uri]("uri")
  }
  object keys {
    val id = key(props.id)
    val uri = key(props.uri)
  }
  object indexes {
  }
}

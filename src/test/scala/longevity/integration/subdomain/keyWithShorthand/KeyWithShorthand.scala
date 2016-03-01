package longevity.integration.subdomain.keyWithShorthand

import longevity.subdomain._
import shorthands._

case class KeyWithShorthand(id: String, uri: Uri) extends Root

object KeyWithShorthand extends RootType[KeyWithShorthand] {
  key(prop[String]("id"))
  key(prop[Uri]("uri"))
}

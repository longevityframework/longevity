package longevity.integration.subdomain.indexWithMultipleProperties

import longevity.subdomain._

case class IndexWithMultipleProperties(
  realm: String,
  name: String)
extends Root

object IndexWithMultipleProperties extends RootType[IndexWithMultipleProperties] {
  index("realm", "name")
}


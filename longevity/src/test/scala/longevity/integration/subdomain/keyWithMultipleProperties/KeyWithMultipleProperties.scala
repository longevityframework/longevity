package longevity.integration.subdomain.keyWithMultipleProperties

import longevity.subdomain._

case class KeyWithMultipleProperties(
  realm: String,
  name: String)
extends RootEntity

object KeyWithMultipleProperties extends RootEntityType[KeyWithMultipleProperties] {
  key("realm", "name")
}


package longevity.integration.subdomain.natKeyWithMultipleProperties

import longevity.subdomain._

case class NatKeyWithMultipleProperties(
  realm: String,
  name: String)
extends RootEntity

object NatKeyWithMultipleProperties extends RootEntityType[NatKeyWithMultipleProperties] {
  natKey("realm", "name")
}


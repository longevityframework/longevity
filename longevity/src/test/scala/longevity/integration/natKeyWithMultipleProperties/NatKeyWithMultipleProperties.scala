package longevity.integration.natKeyWithMultipleProperties

import longevity.subdomain._

case class NatKeyWithMultipleProperties(
  uri: Uri,
  name: String,
  long: LongShorthand,
  associated: Assoc[Associated])
extends RootEntity

object NatKeyWithMultipleProperties extends RootEntityType[NatKeyWithMultipleProperties] {
  natKey("uri")
  natKey("name", "long", "associated")
}


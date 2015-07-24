package longevity.integration.master

import longevity.subdomain._

case class OneAttribute(uri: String) extends RootEntity

object OneAttribute extends RootEntityType[OneAttribute]

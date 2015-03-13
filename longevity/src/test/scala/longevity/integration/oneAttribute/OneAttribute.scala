package longevity.integration.oneAttribute

import longevity.domain._

case class OneAttribute(uri: String) extends RootEntity

object OneAttribute extends RootEntityType[OneAttribute]

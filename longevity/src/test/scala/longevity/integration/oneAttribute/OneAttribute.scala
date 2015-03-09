package longevity.integration.oneAttribute

import longevity.domain._

case class OneAttribute(uri: String) extends Entity

object OneAttribute extends EntityType[OneAttribute]

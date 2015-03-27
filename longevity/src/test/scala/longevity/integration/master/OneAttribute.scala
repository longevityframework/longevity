package longevity.integration.master

import longevity.subdomain._

case class OneAttribute(uri: String) extends Entity

object OneAttribute extends EntityType[OneAttribute]

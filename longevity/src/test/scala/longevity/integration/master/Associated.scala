package longevity.integration.master

import longevity.domain._

case class Associated(uri: String) extends Entity

object Associated extends EntityType[Associated]

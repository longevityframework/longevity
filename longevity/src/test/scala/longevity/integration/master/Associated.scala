package longevity.integration.master

import longevity.domain._

case class Associated(uri: String) extends RootEntity

object Associated extends RootEntityType[Associated]

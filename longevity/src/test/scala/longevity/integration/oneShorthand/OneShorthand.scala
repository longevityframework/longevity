package longevity.integration.oneShorthand

import longevity.domain._

case class OneShorthand(uri: Uri) extends RootEntity

object OneShorthand extends RootEntityType[OneShorthand]

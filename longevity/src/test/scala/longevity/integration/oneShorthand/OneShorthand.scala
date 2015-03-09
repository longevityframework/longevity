package longevity.integration.oneShorthand

import longevity.domain._

case class OneShorthand(uri: Uri) extends Entity

object OneShorthand extends EntityType[OneShorthand]

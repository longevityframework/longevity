package longevity.integration.master

import longevity.domain._

case class OneShorthand(uri: Uri) extends Entity

object OneShorthand extends EntityType[OneShorthand]

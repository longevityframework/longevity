package longevity.integration.master

import longevity.domain._

case class OneShorthand(string: StringShorthand) extends Entity

object OneShorthand extends EntityType[OneShorthand]

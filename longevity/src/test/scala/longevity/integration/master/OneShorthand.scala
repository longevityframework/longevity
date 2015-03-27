package longevity.integration.master

import longevity.subdomain._

case class OneShorthand(string: StringShorthand) extends Entity

object OneShorthand extends EntityType[OneShorthand]

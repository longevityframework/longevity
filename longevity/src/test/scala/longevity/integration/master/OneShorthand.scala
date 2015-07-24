package longevity.integration.master

import longevity.subdomain._

case class OneShorthand(string: StringShorthand) extends RootEntity

object OneShorthand extends RootEntityType[OneShorthand]

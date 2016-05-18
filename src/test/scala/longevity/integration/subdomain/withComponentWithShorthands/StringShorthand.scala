package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain.Shorthand

case class StringShorthand(string: String)

object StringShorthand extends Shorthand[StringShorthand, String]

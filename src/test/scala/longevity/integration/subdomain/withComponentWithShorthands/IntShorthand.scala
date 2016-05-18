package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain.Shorthand

case class IntShorthand(int: Int)

object IntShorthand extends Shorthand[IntShorthand, Int]

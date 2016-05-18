package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain.Shorthand

case class DoubleShorthand(double: Double)

object DoubleShorthand extends Shorthand[DoubleShorthand, Double]

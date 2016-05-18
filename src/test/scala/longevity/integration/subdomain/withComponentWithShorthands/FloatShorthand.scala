package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain.Shorthand

case class FloatShorthand(float: Float)

object FloatShorthand extends Shorthand[FloatShorthand, Float]

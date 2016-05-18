package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain.Shorthand

case class BooleanShorthand(boolean: Boolean)

object BooleanShorthand extends Shorthand[BooleanShorthand, Boolean]

package longevity.integration.subdomain.allShorthands

import longevity.subdomain.Shorthand

case class BooleanShorthand(boolean: Boolean)

object BooleanShorthand extends Shorthand[BooleanShorthand, Boolean]

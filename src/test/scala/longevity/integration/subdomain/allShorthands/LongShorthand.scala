package longevity.integration.subdomain.allShorthands

import longevity.subdomain.Shorthand

case class LongShorthand(long: Long)

object LongShorthand extends Shorthand[LongShorthand, Long]

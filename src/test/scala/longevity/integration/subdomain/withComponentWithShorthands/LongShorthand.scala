package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain.Shorthand

case class LongShorthand(long: Long)

object LongShorthand extends Shorthand[LongShorthand, Long]

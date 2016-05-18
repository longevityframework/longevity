package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain.Shorthand

case class CharShorthand(char: Char)

object CharShorthand extends Shorthand[CharShorthand, Char]

package longevity.integration.subdomain.allShorthands

import longevity.subdomain.Shorthand

case class CharShorthand(char: Char)

object CharShorthand extends Shorthand[CharShorthand, Char]

package longevity.integration.subdomain.keyWithShorthand

import longevity.subdomain.Shorthand

case class Uri(uri: String)

object Uri extends Shorthand[Uri, String]

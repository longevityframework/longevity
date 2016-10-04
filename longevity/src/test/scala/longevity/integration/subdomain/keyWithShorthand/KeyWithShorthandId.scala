package longevity.integration.subdomain.keyWithShorthand

import longevity.subdomain.KeyVal

case class KeyWithShorthandId(
  id: String)
extends KeyVal[KeyWithShorthand, KeyWithShorthandId](
  KeyWithShorthand.keys.id)

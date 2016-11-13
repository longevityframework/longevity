package longevity.integration.subdomain.keyWithShorthand

import longevity.subdomain.KeyVal

case class SecondaryKey(
  id: String,
  uri: Uri)
extends KeyVal[KeyWithShorthand]

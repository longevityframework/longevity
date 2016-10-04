package longevity.integration.subdomain.shorthands

import longevity.subdomain.KeyVal

case class ShorthandsId(
  id: String)
extends KeyVal[Shorthands, ShorthandsId](
  Shorthands.keys.id)

package longevity.integration.subdomain.componentShorthands

import longevity.subdomain.KeyVal

case class WithComponentShorthandsId(
  id: String)
extends KeyVal[WithComponentShorthands](
  WithComponentShorthands.keys.id)

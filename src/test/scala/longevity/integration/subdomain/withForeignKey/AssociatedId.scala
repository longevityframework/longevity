package longevity.integration.subdomain.withForeignKey

import longevity.subdomain.KeyVal

case class AssociatedId(
  id: String)
extends KeyVal[Associated](
  Associated.keys.id)

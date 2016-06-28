package longevity.integration.subdomain.withComponentWithForeignKey

import longevity.subdomain.KeyVal

case class AssociatedId(
  id: String)
extends KeyVal[Associated](
  Associated.keys.id)

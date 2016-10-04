package longevity.integration.subdomain.foreignKey

import longevity.subdomain.KeyVal

case class AssociatedId(
  id: String)
extends KeyVal[Associated, AssociatedId](
  Associated.keys.id)

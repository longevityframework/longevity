package longevity.integration.subdomain.foreignKeySet

import longevity.subdomain.KeyVal

case class AssociatedId(
  id: String)
extends KeyVal[Associated, AssociatedId](
  Associated.keys.id)

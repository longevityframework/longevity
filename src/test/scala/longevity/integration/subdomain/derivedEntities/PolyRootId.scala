package longevity.integration.subdomain.derived

import longevity.subdomain.KeyVal

case class PolyRootId(
  id: String)
extends KeyVal[PolyRoot, PolyRootId](
  PolyRoot.keys.id)

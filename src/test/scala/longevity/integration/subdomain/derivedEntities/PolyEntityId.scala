package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.KeyVal

case class PolyEntityId(
  id: String)
extends KeyVal[FirstDerivedRoot, PolyEntityId](
  FirstDerivedRoot.keys.componentId)

package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.KeyVal

case class PolyRootId(id: String)
extends KeyVal[PolyRoot](PolyRoot.keys.id)

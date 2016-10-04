package longevity.integration.subdomain.derived

import longevity.subdomain.KeyVal

case class PolyEmbeddableId(
  id: String)
extends KeyVal[FirstDerivedRoot, PolyEmbeddableId](
  FirstDerivedRoot.keys.componentId)

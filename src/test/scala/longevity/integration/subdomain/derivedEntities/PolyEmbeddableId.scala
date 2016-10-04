package longevity.integration.subdomain.derived

import longevity.subdomain.KeyVal

case class PolyEmbeddableId(
  id: String)
extends KeyVal[FirstDerivedPersistent, PolyEmbeddableId](
  FirstDerivedPersistent.keys.componentId)

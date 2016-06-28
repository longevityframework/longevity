package longevity.integration.subdomain.withSinglePropComponent

import longevity.subdomain.KeyVal

case class WithSinglePropComponentId(
  id: String)
extends KeyVal[WithSinglePropComponent](
  WithSinglePropComponent.keys.id)



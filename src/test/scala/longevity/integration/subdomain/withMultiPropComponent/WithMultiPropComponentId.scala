package longevity.integration.subdomain.withMultiPropComponent

import longevity.subdomain.KeyVal

case class WithMultiPropComponentId(
  id: String)
extends KeyVal[WithMultiPropComponent](
  WithMultiPropComponent.keys.id)



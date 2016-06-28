package longevity.integration.subdomain.withComponentWithSinglePropComponent

import longevity.subdomain.KeyVal

case class WithComponentWithSinglePropComponentId(
  id: String)
extends KeyVal[WithComponentWithSinglePropComponent](
  WithComponentWithSinglePropComponent.keys.id)

package longevity.integration.subdomain.componentWithOption

import longevity.subdomain.KeyVal

case class WithComponentWithOptionId(
  id: String)
extends KeyVal[WithComponentWithOption, WithComponentWithOptionId](
  WithComponentWithOption.keys.id)

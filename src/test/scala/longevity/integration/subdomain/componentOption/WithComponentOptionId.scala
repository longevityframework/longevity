package longevity.integration.subdomain.componentOption

import longevity.subdomain.KeyVal

case class WithComponentOptionId(
  id: String)
extends KeyVal[WithComponentOption](
  WithComponentOption.keys.id)

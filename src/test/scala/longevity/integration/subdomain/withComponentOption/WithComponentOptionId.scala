package longevity.integration.subdomain.withComponentOption

import longevity.subdomain.KeyVal

case class WithComponentOptionId(
  id: String)
extends KeyVal[WithComponentOption](
  WithComponentOption.keys.id)

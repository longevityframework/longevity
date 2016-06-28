package longevity.integration.subdomain.withComponentSet

import longevity.subdomain.KeyVal

case class WithComponentSetId(
  id: String)
extends KeyVal[WithComponentSet](
  WithComponentSet.keys.id)

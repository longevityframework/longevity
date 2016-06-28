package longevity.integration.subdomain.withComponentWithForeignKey

import longevity.subdomain.KeyVal

case class WithComponentWithForeignKeyId(
  id: String)
extends KeyVal[WithComponentWithForeignKey](
  WithComponentWithForeignKey.keys.id)
